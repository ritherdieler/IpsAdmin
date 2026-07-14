package com.dscorp.ispadmin.presentation.ui.features.login

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class LoginTestTagsTest {

    @Test
    fun `tags interactivos del flujo login estan definidos y no vacios`() {
        assertThat(LoginTestTags.interactive).containsExactly(
            LoginTestTags.USERNAME,
            LoginTestTags.PASSWORD,
            LoginTestTags.PASSWORD_VISIBILITY,
            LoginTestTags.REMEMBER_SESSION,
            LoginTestTags.SUBMIT,
            LoginTestTags.BIOMETRIC,
            LoginTestTags.CREATE_ACCOUNT
        ).inOrder()
        LoginTestTags.interactive.forEach { tag ->
            assertThat(tag).isNotEmpty()
        }
    }

    @Test
    fun `descripciones de contenido no interactivo estan definidas`() {
        assertThat(LoginContentDescriptions.LOGO).isNotEmpty()
        assertThat(LoginContentDescriptions.BIOMETRIC_ICON).isNotEmpty()
    }

    @Test
    fun `Login aplica testTags interactivos y contentDescriptions requeridos`() {
        val loginSource = File(
            "src/main/java/com/dscorp/ispadmin/presentation/ui/features/login/Login.kt"
        ).readText()

        listOf(
            "LoginTestTags.USERNAME",
            "LoginTestTags.PASSWORD",
            "LoginTestTags.PASSWORD_VISIBILITY",
            "LoginTestTags.REMEMBER_SESSION",
            "LoginTestTags.SUBMIT",
            "LoginTestTags.BIOMETRIC",
            "LoginTestTags.CREATE_ACCOUNT"
        ).forEach { ref ->
            assertThat(loginSource).contains(ref)
        }
        assertThat(loginSource).contains("contentDescription = LoginContentDescriptions.LOGO")
        assertThat(loginSource).contains("contentDescription = LoginContentDescriptions.BIOMETRIC_ICON")
        assertThat(loginSource).doesNotContain("ObservabilityComposeClick.report")
        assertThat(loginSource).contains("LoginTestTags.SUBMIT")
        assertThat(loginSource).contains(".testTag(LoginTestTags.SUBMIT)")
        assertThat(loginSource).contains(".testTag(LoginTestTags.USERNAME)")
        assertThat(loginSource).contains(".testTag(LoginTestTags.PASSWORD)")
        assertThat(loginSource).contains(".testTag(LoginTestTags.PASSWORD_VISIBILITY)")
        assertThat(loginSource).contains(".testTag(LoginTestTags.REMEMBER_SESSION)")
        assertThat(loginSource).contains(".testTag(LoginTestTags.CREATE_ACCOUNT)")
        assertThat(loginSource).contains("testTag = LoginTestTags.BIOMETRIC")
    }
}
