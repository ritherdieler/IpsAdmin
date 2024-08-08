package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.os.Bundle
import android.view.View
import androidx.compose.ui.text.capitalize
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentDashBoardBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.dashboard.graphics.DashBoardActiveSubscriptionsByMonthLinearChart
import com.dscorp.ispadmin.presentation.ui.features.dashboard.graphics.DashBoardGrossRevenueByMonthLinearChart
import com.dscorp.ispadmin.presentation.ui.features.dashboard.graphics.DashboardPaymentMethodsBarChart
import com.dscorp.ispadmin.presentation.ui.features.dashboard.graphics.DashboardPieChart
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Locale

class DashBoardFragment : BaseFragment<DashBoardDataUiState, FragmentDashBoardBinding>() {

    override val viewModel: DashBoardViewModel by viewModel()
    override val binding by lazy { FragmentDashBoardBinding.inflate(layoutInflater) }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupBindings()
    }

    private fun setupBindings() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    override fun handleState(state: DashBoardDataUiState) {
        when (state) {
            is DashBoardDataUiState.DashBoardData -> handleDashBoardData(state)
            is DashBoardDataUiState.CutServiceSuccess -> showSuccessDialog(getString(R.string.cutt_off_iniated))
            is DashBoardDataUiState.InvalidPasswordError -> showErrorDialog(state.error)
        }
    }

    private fun handleDashBoardData(state: DashBoardDataUiState.DashBoardData) {
        lifecycleScope.launch {
            binding.dashboardDataResponse = state.response

            binding.fiberTvCableNewSubscriptions.text =
                state.response.subscriptionsResume.cableTvInstallations.toString()
            binding.fiberNewSubscriptions.text =
                state.response.subscriptionsResume.fiberInternetInstallations.toString()
            binding.wirelessNewSubscriptions.text =
                state.response.subscriptionsResume.wirelessInternetInstallations.toString()
            binding.cancelled.text =
                state.response.subscriptionsResume.canceledSubscriptions.toString()
            //si no se hace esto no se muestra el grafico de barras
            binding.paymentMethodsBarChart.visibility = View.GONE
            binding.paymentMethodsBarChart.visibility = View.VISIBLE
//
//        binding.monthlyCollectsBarChart.visibility = View.GONE
//        binding.monthlyCollectsBarChart.visibility = View.VISIBLE
            binding.shimmerFrameLayout.clearShimmer()



            val collectResumeData = state.response
            DashboardPieChart(binding.chart1).setData(collectResumeData)

            val subscriptionHistoryData = state.response.subscriptionsHistoryStatics
            DashBoardActiveSubscriptionsByMonthLinearChart(
                binding.activeSubscriptionHistoryLinearChart,
                requireContext()
            ).setData(subscriptionHistoryData)


            val grossRevenueHistoryData = state.response.grossRevenueHistoryStatics

            DashBoardGrossRevenueByMonthLinearChart(
                binding.grossRevenueHistoryLinearChart,
                requireContext()
            ).setData(grossRevenueHistoryData.sortedBy { it.billingDate })

            setElectronicPaymentData(state)

            setMonthlyCollectData(state)
        }


    }

    private fun setElectronicPaymentData(state: DashBoardDataUiState.DashBoardData) {
        val paymentMethodsData = state.response.paymentResume
        DashboardPaymentMethodsBarChart(binding.paymentMethodsBarChart).setData(
            paymentMethodsData
        )
    }

    private fun setMonthlyCollectData(state: DashBoardDataUiState.DashBoardData) {
        val monthlyCollectsResume =
            state.response.monthlyCollects.mapIndexed { index, monthlyCollectsResume ->
                BarEntry(
                    index.toFloat(),
                    floatArrayOf(
                        monthlyCollectsResume.totalDiscount.toFloat(),
                        monthlyCollectsResume.totalRaised.toFloat(),
                        monthlyCollectsResume.totalReceivables.toFloat()
                    ),
                    ResourcesCompat.getDrawable(resources, R.drawable.star, null)
                )
            }
        val xAxisLabels =
            state.response.monthlyCollects.map {
                //get month name
                (Calendar.getInstance().apply { time = it.date }.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale("es", "PE")
                )?.substring(0, 3)?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                } + ".")


            }
        DashboardPaymentMethodsBarChart(binding.monthlyCollectsBarChart).setStackedData(
            monthlyCollectsResume, xAxisLabels
        )
    }


}

private fun ShimmerFrameLayout.clearShimmer() {
    stopShimmer()
    hideShimmer()
}
