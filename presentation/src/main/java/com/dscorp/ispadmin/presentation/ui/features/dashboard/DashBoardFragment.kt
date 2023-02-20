package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.databinding.FragmentDashBoardBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment

class DashBoardFragment : BaseFragment() {
    val binding by lazy { FragmentDashBoardBinding.inflate(layoutInflater) }

    private val viewModel: DashBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getDashBoardData()

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DashBoardDataUiState.DashBoardData -> binding.dashboardDataResponse = state.response
                is DashBoardDataUiState.DashBoardDataError -> showErrorDialog(state.message)
            }
        }

        return binding.root
    }


}