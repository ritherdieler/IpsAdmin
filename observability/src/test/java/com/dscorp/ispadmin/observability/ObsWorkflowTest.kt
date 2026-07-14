package com.dscorp.ispadmin.observability

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObsWorkflowTest {

    @Test
    fun `active tags include id name and category without status`() {
        val workflow = ObsWorkflow(
            id = "wf-1",
            name = "login",
            category = "auth",
            context = mapOf("screen" to "LoginScreen"),
            startedAt = 1000L
        )

        val tags = ObsWorkflowTags.active(workflow)

        assertThat(tags).containsExactly(
            ObsWorkflowTags.ID, "wf-1",
            ObsWorkflowTags.NAME, "login",
            ObsWorkflowTags.CATEGORY, "auth"
        )
        assertThat(tags).doesNotContainKey(ObsWorkflowTags.STATUS)
    }

    @Test
    fun `closed tags include status`() {
        val workflow = ObsWorkflow(
            id = "wf-2",
            name = "login",
            category = "auth",
            context = emptyMap(),
            startedAt = 1000L
        )

        val tags = ObsWorkflowTags.closed(workflow, WorkflowStatus.SUCCESS)

        assertThat(tags[ObsWorkflowTags.STATUS]).isEqualTo("success")
        assertThat(tags[ObsWorkflowTags.ID]).isEqualTo("wf-2")
    }

    @Test
    fun `context snapshot exposes workflow identity`() {
        val workflow = ObsWorkflow(
            id = "wf-3",
            name = "login",
            category = "auth",
            context = mapOf("attempt" to 1),
            startedAt = 42L
        )

        val snapshot = ObsWorkflowTags.contextSnapshot(workflow)

        assertThat(snapshot["id"]).isEqualTo("wf-3")
        assertThat(snapshot["name"]).isEqualTo("login")
        assertThat(snapshot["category"]).isEqualTo("auth")
        assertThat(snapshot["startedAt"]).isEqualTo(42L)
        assertThat(snapshot["attempt"]).isEqualTo(1)
    }
}
