package com.dscorp.ispadmin.presentation.ui.features.plan.edit

import com.example.cleanarchitecture.domain.entity.PlanResponse

sealed class EditPlanUiState {
    class EditPlanUpdateSuccess(val plan: PlanResponse):EditPlanUiState()
}