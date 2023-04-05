package com.dscorp.ispadmin.presentation.ui.features.plan.planlist

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlanListViewModel : ViewModel(), KoinComponent {

    val repository: IRepository by inject()
    val responseLiveData = MutableLiveData<PlanListUiState>()

    val recyclerVisibility = MutableLiveData(View.GONE)

    init {
        initGetSubscriptions()
    }

    private fun initGetSubscriptions() = viewModelScope.launch {
        try {
            val napBoxList = repository.getPlans()

            recyclerVisibility.value =
                if (napBoxList.isNotEmpty()) View.VISIBLE else View.GONE

            responseLiveData.postValue(PlanListUiState.OnPlanListFound(napBoxList))
        } catch (error: Exception) {
            error.printStackTrace()
            responseLiveData.postValue(PlanListUiState.OnError(error))
        }
    }
}

sealed class PlanListUiState {
    class OnPlanListFound(val planList: List<PlanResponse>) : PlanListUiState()
    class OnError(val error: Exception) : PlanListUiState()
}
