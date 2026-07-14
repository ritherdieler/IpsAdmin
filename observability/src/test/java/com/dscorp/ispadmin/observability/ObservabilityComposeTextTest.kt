package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObservabilityComposeTextTest {

    private lateinit var store: InMemoryEventStore
    private lateinit var client: ObservabilityClient
    private lateinit var uiCapture: ObservabilityUiCapture
    private val gson = Gson()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        store = InMemoryEventStore()
        client = ObservabilityClient(
            api = mockk(relaxed = true),
            queue = store,
            contextProvider = ObservabilityContextProvider(
                object : ObsAppInfo {
                    override fun environment() = "dev"
                    override fun release() = "1.0.0"
                    override fun versionName() = "1.0.0"
                    override fun versionCode() = 1
                    override fun flavor() = "dev"
                },
                ObsUserProvider { null }
            ),
            gson = gson,
            apiKey = "",
            workScheduler = ObservabilityFlushScheduler { },
            config = ObservabilityConfig(apiKey = "", sanitizePayloads = false),
            coroutineScope = CoroutineScope(UnconfinedTestDispatcher())
        )
        uiCapture = ObservabilityUiCapture(
            clientProvider = lazyOf(client),
            scope = CoroutineScope(testDispatcher),
            textChangeDebounceMs = 2000L
        )
        ObservabilityComposeText.bind(uiCapture)
    }

    @Test
    fun `report emite text_change con tag estable y label`() = runTest(testDispatcher) {
        ObservabilityComposeText.report(
            tag = "login_username",
            label = "Usuario",
            value = "dscorp"
        )
        runCurrent()
        advanceTimeBy(2000L)
        runCurrent()

        client.reportLog("probe")
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }.single()
        val crumb = event.breadcrumbs?.single()
        assertThat(crumb?.get("category")).isEqualTo(ObsBreadcrumbCategory.UI)
        @Suppress("UNCHECKED_CAST")
        val data = crumb?.get("data") as Map<String, Any?>
        assertThat(data["type"]).isEqualTo(ObsUiEventType.TEXT_CHANGE)
        assertThat(data["target"]).isEqualTo("login_username")
        assertThat(data["tag"]).isEqualTo("login_username")
        assertThat(data["label"]).isEqualTo("Usuario")
        assertThat(data["value"]).isEqualTo("dscorp")
        assertThat(data["source"]).isEqualTo("compose")
    }
}
