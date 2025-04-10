package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import com.example.cleanarchitecture.domain.entity.PlanResponse

sealed class PlanListUiState {
    class OnPlanListFound(val planList: List<PlanResponse>) : PlanListUiState()
}