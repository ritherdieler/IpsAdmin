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

    @Test
    fun `prod flavor prefiere OBS_API_KEY_ANDROID sobre OBS_API_KEY legacy dev`() {
        assertThat(
            resolveObservabilityApiKey(
                flavor = "prod",
                buildConfigKey = "dev-obs-android-key",
                buildConfigAndroidKey = "obs_android_prod_key"
            )
        ).isEqualTo("obs_android_prod_key")
    }

    @Test
    fun `dev flavor ignora keys de buildConfig y usa dev-obs-android-key`() {
        assertThat(
            resolveObservabilityApiKey(
                flavor = "dev",
                buildConfigKey = "dev-obs-android-key",
                buildConfigAndroidKey = "obs_android_prod_key"
            )
        ).isEqualTo("dev-obs-android-key")
    }
}
