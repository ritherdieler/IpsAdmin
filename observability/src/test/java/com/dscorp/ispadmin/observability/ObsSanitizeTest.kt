package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObsSanitizeTest {

    @Test
    fun `strips sensitive keys when sanitizing`() {
        val input = mapOf(
            "username" to "ana",
            "password" to "secret",
            "token" to "abc",
            "nestedSafe" to "ok"
        )

        val result = ObsSanitize.sanitizeMap(input, sanitize = true)

        assertThat(result).containsEntry("username", "ana")
        assertThat(result).containsEntry("nestedSafe", "ok")
        assertThat(result).containsEntry("password", "[redacted]")
        assertThat(result).containsEntry("token", "[redacted]")
    }

    @Test
    fun `keeps values when sanitize is false`() {
        val input = mapOf("password" to "secret")
        val result = ObsSanitize.sanitizeMap(input, sanitize = false)
        assertThat(result).containsEntry("password", "secret")
    }
}
