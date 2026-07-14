package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObservabilityComposeTargetTest {

    @Test
    fun `resolve prioriza testTag sobre texto y className`() {
        val result = ObservabilityComposeTarget.resolve(
            testTag = "login_submit",
            contentDescription = "Iniciar sesion",
            text = "Iniciar sesion",
            className = "android.widget.Button"
        )
        assertThat(result).isEqualTo("login_submit")
    }

    @Test
    fun `resolve usa contentDescription si no hay testTag`() {
        val result = ObservabilityComposeTarget.resolve(
            testTag = null,
            contentDescription = "Logo de ISP Admin",
            text = null,
            className = "android.widget.ImageView"
        )
        assertThat(result).isEqualTo("Logo de ISP Admin")
    }

    @Test
    fun `resolve no usa hosts genericos como target`() {
        assertThat(ObservabilityComposeTarget.isGenericHost("AndroidComposeView")).isTrue()
        assertThat(ObservabilityComposeTarget.isGenericHost("AndroidViewsHandler")).isTrue()
        assertThat(ObservabilityComposeTarget.isGenericHost("login_submit")).isFalse()
        assertThat(
            ObservabilityComposeTarget.resolve(className = "androidx.compose.ui.platform.AndroidComposeView")
        ).isEqualTo("compose_unlabeled")
    }

    @Test
    fun `isComposeHost detecta contenedores compose`() {
        assertThat(
            ObservabilityComposeTarget.isComposeHost("androidx.compose.ui.platform.AndroidComposeView")
        ).isTrue()
        assertThat(
            ObservabilityComposeTarget.isComposeHost("androidx.compose.ui.viewinterop.AndroidViewsHandler")
        ).isTrue()
        assertThat(ObservabilityComposeTarget.isComposeHost("android.widget.Button")).isFalse()
    }
}
