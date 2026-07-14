package com.dscorp.ispadmin.observability

enum class WorkflowStatus(val wireValue: String) {
    SUCCESS("success"),
    FAILED("failed"),
    INTERRUPTED("interrupted")
}

data class ObsWorkflow(
    val id: String,
    val name: String,
    val category: String,
    val context: Map<String, Any?>,
    val startedAt: Long
)

object ObsWorkflowTags {
    const val ID = "workflowId"
    const val NAME = "workflowName"
    const val CATEGORY = "workflowCategory"
    const val STATUS = "workflowStatus"

    fun active(workflow: ObsWorkflow): Map<String, Any?> = mapOf(
        ID to workflow.id,
        NAME to workflow.name,
        CATEGORY to workflow.category
    )

    fun closed(workflow: ObsWorkflow, status: WorkflowStatus): Map<String, Any?> =
        active(workflow) + (STATUS to status.wireValue)

    fun contextSnapshot(workflow: ObsWorkflow): Map<String, Any?> =
        linkedMapOf<String, Any?>(
            "id" to workflow.id,
            "name" to workflow.name,
            "category" to workflow.category,
            "startedAt" to workflow.startedAt
        ).apply { putAll(workflow.context) }
}
