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
class ObservabilityUiCaptureTest {

    private lateinit var store: InMemoryEventStore
    private lateinit var client: ObservabilityClient
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
    }

    private fun capture(): ObservabilityUiCapture =
        ObservabilityUiCapture(
            clientProvider = lazyOf(client),
            scope = CoroutineScope(testDispatcher),
            textChangeDebounceMs = 2000L
        )

    private fun uiCrumbs(): List<Map<String, Any?>> {
        client.reportLog("probe")
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }
            .first { it.eventType == "log" }
        return event.breadcrumbs
            .orEmpty()
            .filter { it["category"] == ObsBreadcrumbCategory.UI }
    }

    @Test
    fun `capture emite breadcrumb ui sin workflow`() {
        val uiCapture = capture()
        uiCapture.capture(
            type = ObsUiEventType.CLICK,
            target = "login_button",
            value = null,
            data = mapOf("class" to "Button")
        )

        val crumb = uiCrumbs().single()
        assertThat(crumb["message"]).isEqualTo(ObsUiEventType.CLICK)
        @Suppress("UNCHECKED_CAST")
        val data = crumb["data"] as Map<String, Any?>
        assertThat(data["target"]).isEqualTo("login_button")
        assertThat(data["type"]).isEqualTo(ObsUiEventType.CLICK)
        assertThat(client.currentWorkflowId()).isNull()
    }

    @Test
    fun `text_change se emite tras debounce con el ultimo valor`() = runTest(testDispatcher) {
        val uiCapture = capture()
        uiCapture.capture(type = ObsUiEventType.TEXT_CHANGE, target = "Usuario", value = "d")
        uiCapture.capture(type = ObsUiEventType.TEXT_CHANGE, target = "Usuario", value = "ds")
        uiCapture.capture(type = ObsUiEventType.TEXT_CHANGE, target = "Usuario", value = "dscorp")
        runCurrent()
        assertThat(uiCrumbs()).isEmpty()

        advanceTimeBy(2000L)
        runCurrent()

        store.clear()
        val crumb = uiCrumbs().single()
        @Suppress("UNCHECKED_CAST")
        val data = crumb["data"] as Map<String, Any?>
        assertThat(data["type"]).isEqualTo(ObsUiEventType.TEXT_CHANGE)
        assertThat(data["target"]).isEqualTo("Usuario")
        assertThat(data["value"]).isEqualTo("dscorp")
    }

    @Test
    fun `click flushea text_change pendiente`() = runTest(testDispatcher) {
        val uiCapture = capture()
        uiCapture.capture(type = ObsUiEventType.TEXT_CHANGE, target = "Contraseña", value = "nohacker")
        runCurrent()
        uiCapture.capture(type = ObsUiEventType.CLICK, target = "Iniciar sesion")
        runCurrent()

        val crumbs = uiCrumbs()
        assertThat(crumbs).hasSize(2)
        @Suppress("UNCHECKED_CAST")
        val textData = crumbs.first()["data"] as Map<String, Any?>
        assertThat(textData["value"]).isEqualTo("nohacker")
        assertThat(crumbs.last()["message"]).isEqualTo(ObsUiEventType.CLICK)
    }

    @Test
    fun `clicks duplicados del mismo target se deduplican`() {
        var now = 1_000L
        val uiCapture = ObservabilityUiCapture(
            clientProvider = lazyOf(client),
            scope = CoroutineScope(testDispatcher),
            clickDedupeMs = 400L,
            clock = { now }
        )
        uiCapture.capture(type = ObsUiEventType.CLICK, target = "login_submit")
        uiCapture.capture(type = ObsUiEventType.CLICK, target = "login_submit")
        now = 1_500L
        uiCapture.capture(type = ObsUiEventType.CLICK, target = "login_submit")

        store.clear()
        client.reportLog("probe")
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }
            .first { it.eventType == "log" }
        val clicks = event.breadcrumbs.orEmpty()
            .filter { it["category"] == ObsBreadcrumbCategory.UI && it["message"] == ObsUiEventType.CLICK }
        assertThat(clicks).hasSize(2)
    }

    @Test
    fun `capture funciona con workflow activo`() = runTest(testDispatcher) {
        val workflowId = client.startWorkflow("login", "auth")
        store.clear()
        val uiCapture = capture()
        uiCapture.capture(type = ObsUiEventType.TEXT_CHANGE, target = "password", value = "secret")
        advanceTimeBy(2000L)
        runCurrent()

        store.clear()
        val crumb = uiCrumbs().single()
        @Suppress("UNCHECKED_CAST")
        val data = crumb["data"] as Map<String, Any?>
        assertThat(data["value"]).isEqualTo("secret")
        assertThat(data[ObsWorkflowTags.ID]).isEqualTo(workflowId)
    }
}
