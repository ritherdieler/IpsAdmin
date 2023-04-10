package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.dscorp.ispadmin.presentation.ui.features.base.BaseUiState
import com.dscorp.ispadmin.presentation.ui.features.base.BaseViewModel
import com.example.data2.data.repository.IRepository
import org.koin.core.component.KoinComponent

class PlanListViewModel(
    val repository: IRepository,
) : BaseViewModel<PlanListUiState>(), KoinComponent {

    val recyclerVisibility = MutableLiveData(View.GONE)

    fun getPlans() = executeWithProgress {
        val response = repository.getPlans()
        recyclerVisibility.value =
            if (response.isNotEmpty()) View.VISIBLE else View.GONE
        uiState.value = BaseUiState(uiState = PlanListUiState.OnPlanListFound(response))
    }


}

