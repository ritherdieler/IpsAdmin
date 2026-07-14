package com.dscorp.ispadmin.di

import com.dscorp.ispadmin.BuildConfig
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObservabilityDevApiKeyTest {

    @Test
    fun `dev flavor usa la api key local del backend`() {
        assertThat(resolveObservabilityApiKey("dev", "obs_android_from_secrets"))
            .isEqualTo("dev-obs-android-key")
    }

    @Test
    fun `prod flavor conserva la key de buildConfig`() {
        assertThat(resolveObservabilityApiKey("prod", "obs_android_prod"))
            .isEqualTo("obs_android_prod")
    }
}
