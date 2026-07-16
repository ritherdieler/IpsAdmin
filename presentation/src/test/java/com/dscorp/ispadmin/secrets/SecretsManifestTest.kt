package com.dscorp.ispadmin.secrets

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class SecretsManifestTest {

    private val manifestFile: File by lazy {
        val candidates = listOf(
            File("src/main/AndroidManifest.xml"),
            File("presentation/src/main/AndroidManifest.xml")
        )
        candidates.first { it.exists() }
    }

    @Test
    fun `manifest no contiene api keys de Google Maps hardcodeadas`() {
        val content = manifestFile.readText()
        assertThat(content).doesNotContainMatch("""AIzaSy[A-Za-z0-9_-]+""")
    }

    @Test
    fun `manifest usa placeholder MAPS_API_KEY para Google Maps`() {
        val content = manifestFile.readText()
        assertThat(content).contains("android:value=\"\${MAPS_API_KEY}\"")
    }
}
