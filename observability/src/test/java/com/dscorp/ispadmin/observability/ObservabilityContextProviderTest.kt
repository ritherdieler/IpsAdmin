package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObservabilityContextProviderTest {

    @Test
    fun `environment and device come from injected app info`() {
        val appInfo = object : ObsAppInfo {
            override fun environment() = "dev"
            override fun release() = "1.0.0 (1)"
            override fun versionName() = "1.0.0"
            override fun versionCode() = 1
            override fun flavor() = "dev"
        }
        val userProvider = ObsUserProvider {
            mapOf("id" to 42, "username" to "tech")
        }
        val provider = ObservabilityContextProvider(appInfo, userProvider)

        assertThat(provider.environment()).isEqualTo("dev")
        assertThat(provider.release()).isEqualTo("1.0.0 (1)")
        assertThat(provider.user()).containsEntry("id", 42)
        assertThat(provider.device()).containsEntry("flavor", "dev")
        assertThat(provider.device()).containsEntry("appVersion", "1.0.0")
        assertThat(provider.userAgent()).contains("ispadmin-android/1.0.0")
    }

    @Test
    fun `user is null when provider returns null`() {
        val appInfo = object : ObsAppInfo {
            override fun environment() = "prod"
            override fun release() = "2.0.0 (2)"
            override fun versionName() = "2.0.0"
            override fun versionCode() = 2
            override fun flavor() = "prod"
        }
        val provider = ObservabilityContextProvider(appInfo, ObsUserProvider { null })

        assertThat(provider.user()).isNull()
    }
}
