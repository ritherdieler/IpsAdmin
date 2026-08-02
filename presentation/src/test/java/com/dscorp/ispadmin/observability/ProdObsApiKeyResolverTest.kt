package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProdObsApiKeyResolverTest {

    @Test
    fun prefiereObsApiKeyAndroidSobreLegacyObsApiKey() {
        assertThat(
            ProdObsApiKeyResolver.resolve(
                androidKey = "android-prod-key",
                legacyKey = "legacy-key"
            )
        ).isEqualTo("android-prod-key")
    }

    @Test
    fun usaLegacyCuandoAndroidEstaVacio() {
        assertThat(
            ProdObsApiKeyResolver.resolve(androidKey = "", legacyKey = "legacy-key")
        ).isEqualTo("legacy-key")
    }

    @Test
    fun devuelveVacioSiFaltanAmbas() {
        assertThat(
            ProdObsApiKeyResolver.resolve(androidKey = "  ", legacyKey = "")
        ).isEmpty()
    }
}
