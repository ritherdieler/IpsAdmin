package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test

class ObservabilityComposeClickTest {

    private lateinit var store: InMemoryEventStore
    private lateinit var client: ObservabilityClient
    private val gson = Gson()

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
        ObservabilityComposeClick.bind(
            ObservabilityUiCapture(clientProvider = lazyOf(client))
        )
    }

    @Test
    fun `report emite click con tag estable`() {
        ObservabilityComposeClick.report(tag = "login_submit", label = "Iniciar sesion")
        client.reportLog("probe")
        val event = store.readAll().map { gson.fromJson(it, ObsEventDto::class.java) }
            .first { it.eventType == "log" }
        val crumb = event.breadcrumbs.orEmpty().first { it["category"] == ObsBreadcrumbCategory.UI }
        assertThat(crumb["message"]).isEqualTo(ObsUiEventType.CLICK)
        @Suppress("UNCHECKED_CAST")
        val data = crumb["data"] as Map<String, Any?>
        assertThat(data["target"]).isEqualTo("login_submit")
        assertThat(data["tag"]).isEqualTo("login_submit")
        assertThat(data["label"]).isEqualTo("Iniciar sesion")
        assertThat(data["source"]).isEqualTo("compose")
    }
}
