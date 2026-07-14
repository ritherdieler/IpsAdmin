package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Test

class ObservabilityApiKeyInterceptorTest {

    @Test
    fun `inyecta X-Obs-Api-Key cuando falta`() {
        val chain = mockk<Interceptor.Chain>()
        val original = Request.Builder().url("http://localhost/observability/events").build()
        val proceeded = slot<Request>()
        every { chain.request() } returns original
        every { chain.proceed(capture(proceeded)) } returns mockk(relaxed = true)

        ObservabilityApiKeyInterceptor("dev-obs-android-key").intercept(chain)

        assertThat(proceeded.captured.header("X-Obs-Api-Key")).isEqualTo("dev-obs-android-key")
    }

    @Test
    fun `no sobrescribe header existente`() {
        val chain = mockk<Interceptor.Chain>()
        val original = Request.Builder()
            .url("http://localhost/observability/events")
            .header("X-Obs-Api-Key", "from-retrofit")
            .build()
        every { chain.request() } returns original
        every { chain.proceed(original) } returns mockk(relaxed = true)

        ObservabilityApiKeyInterceptor("from-interceptor").intercept(chain)

        verify(exactly = 1) { chain.proceed(original) }
    }
}
