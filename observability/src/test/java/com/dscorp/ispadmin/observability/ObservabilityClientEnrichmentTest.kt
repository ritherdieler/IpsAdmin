package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObservabilityClientEnrichmentTest {

    private lateinit var store: InMemoryEventStore
    private lateinit var client: ObservabilityClient
    private val gson = Gson()

    @Before
    fun setUp() {
        store = InMemoryEventStore()
        val appInfo = object : ObsAppInfo {
            override fun environment() = "dev"
            override fun release() = "1.0.0 (1)"
            override fun versionName() = "1.0.0"
            override fun versionCode() = 1
            override fun flavor() = "dev"
        }
        client = ObservabilityClient(
            api = mockk(relaxed = true),
            queue = store,
            contextProvider = ObservabilityContextProvider(appInfo, ObsUserProvider { null }),
            gson = gson,
            apiKey = "",
            workScheduler = ObservabilityFlushScheduler { },
            config = ObservabilityConfig(apiKey = "", sanitizePayloads = true),
            coroutineScope = CoroutineScope(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun `interruptActiveWorkflow closes as interrupted`() {
        val id = client.startWorkflow("login", "auth")
        store.clear()

        client.interruptActiveWorkflow("crash")

        assertThat(client.currentWorkflowId()).isNull()
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }.single()
        assertThat(event.eventType).isEqualTo("workflow_end")
        assertThat(event.tags?.get(ObsWorkflowTags.ID)).isEqualTo(id)
        assertThat(event.tags?.get(ObsWorkflowTags.STATUS)).isEqualTo("interrupted")
        assertThat(event.message).isEqualTo("crash")
    }

    @Test
    fun `recordCrash interrupts active workflow`() {
        client.startWorkflow("login", "auth")
        store.clear()

        client.recordCrash(RuntimeException("boom"))

        val events = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }
        assertThat(events.any { it.eventType == "workflow_end" }).isTrue()
        assertThat(events.any { it.eventType == "crash" }).isTrue()
        assertThat(client.currentWorkflowId()).isNull()
    }

    @Test
    fun `currentWorkflowTags exposes active tags`() {
        val id = client.startWorkflow("login", "auth")
        assertThat(client.currentWorkflowTags()).containsEntry(ObsWorkflowTags.ID, id)
        client.endWorkflow(WorkflowStatus.SUCCESS)
        assertThat(client.currentWorkflowTags()).isNull()
    }

    @Test
    fun `startWorkflow sanitizes sensitive context`() {
        client.startWorkflow(
            name = "login",
            category = "auth",
            context = mapOf("password" to "secret", "username" to "ana")
        )
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }.single()
        assertThat(event.context?.get("password")).isEqualTo("[redacted]")
        assertThat(event.context?.get("username")).isEqualTo("ana")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RunWorkflowTest {

    @Test
    fun `runWorkflow ends success on block result`() = runTest {
        val client = mockk<ObservabilityClient>(relaxed = true)
        io.mockk.every { client.currentWorkflowId() } returnsMany listOf(null, "wf-1")

        val result = runWorkflow(
            client = client,
            name = "login",
            category = "auth"
        ) { "ok" }

        assertThat(result.getOrNull()).isEqualTo("ok")
        verify { client.startWorkflow("login", "auth", emptyMap()) }
        verify { client.endWorkflow(WorkflowStatus.SUCCESS, any(), any()) }
    }

    @Test
    fun `runWorkflow ends failed on exception`() = runTest {
        val client = mockk<ObservabilityClient>(relaxed = true)

        val result = runWorkflow(
            client = client,
            name = "login",
            category = "auth"
        ) { throw IllegalStateException("nope") }

        assertThat(result.isFailure).isTrue()
        verify { client.endWorkflow(WorkflowStatus.FAILED, any(), any()) }
    }

    @Test
    fun `runWorkflow rethrows cancellation`() = runTest {
        val client = mockk<ObservabilityClient>(relaxed = true)
        try {
            runWorkflow(client, "login", "auth") { throw CancellationException() }
            org.junit.Assert.fail("expected cancellation")
        } catch (_: CancellationException) {
        }
        verify(exactly = 0) { client.endWorkflow(any(), any(), any()) }
    }
}
