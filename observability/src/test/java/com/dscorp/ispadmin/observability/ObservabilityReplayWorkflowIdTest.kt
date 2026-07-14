package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ObservabilityReplayWorkflowIdTest {

    @Test
    fun `captureAndUpload envia workflowId al api`() = runTest {
        val api = mockk<ObservabilityApi>()
        coEvery {
            api.uploadReplay(
                apiKey = any(),
                contentEncoding = any(),
                format = any(),
                sessionId = any(),
                durationMs = any(),
                workflowId = any(),
                body = any()
            )
        } returns Response.success(ObsReplayUploadResponse(id = 9L))

        val recorder = mockk<ObservabilityScreenRecorder>()
        every { recorder.snapshot() } returns ObsReplaySnapshot(
            width = 10,
            height = 20,
            durationMs = 100L,
            frames = listOf(ObsReplayFrame(t = 1L, img = "abc"))
        )
        every { recorder.clear() } returns Unit

        val sender = ObservabilityReplaySender(
            api = api,
            recorder = recorder,
            gson = Gson(),
            apiKey = "key",
            config = ObservabilityReplayConfig(enableReplay = true)
        )

        val id = sender.captureAndUpload("sess-1", workflowId = "wf-42")
        assertThat(id).isEqualTo(9L)
        coVerify {
            api.uploadReplay(
                apiKey = "key",
                contentEncoding = "gzip",
                format = "frames",
                sessionId = "sess-1",
                durationMs = 100L,
                workflowId = "wf-42",
                body = any<RequestBody>()
            )
        }
    }

    @Test
    fun `reportError captura workflowId antes del upload`() = runTest {
        val replaySender = mockk<ObservabilityReplaySender>()
        coEvery { replaySender.captureAndUpload(any(), any()) } returns 1L
        val store = InMemoryEventStore()
        val client = ObservabilityClient(
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
            gson = Gson(),
            apiKey = "",
            workScheduler = ObservabilityFlushScheduler { },
            replaySender = replaySender,
            config = ObservabilityConfig(apiKey = "", sanitizePayloads = false),
            coroutineScope = CoroutineScope(UnconfinedTestDispatcher())
        )
        val workflowId = client.startWorkflow("login", "auth")
        client.reportError(RuntimeException("boom"))
        coVerify { replaySender.captureAndUpload(any(), workflowId) }
    }
}
