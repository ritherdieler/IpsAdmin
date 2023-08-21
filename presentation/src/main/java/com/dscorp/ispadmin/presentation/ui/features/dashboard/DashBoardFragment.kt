package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentDashBoardBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.User
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashBoardFragment : BaseFragment<DashBoardDataUiState, FragmentDashBoardBinding>() {

    override val viewModel: DashBoardViewModel by viewModel()
    override val binding by lazy { FragmentDashBoardBinding.inflate(layoutInflater) }

    override fun onViewReady(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        viewModel.userSession?.type?.let {
            if (it == User.UserType.ADMIN) {
                binding.btnStartCut.visibility = View.VISIBLE
            }
            binding.btnStartCut.setOnClickListener {
                showCutConfirmationDialog()
            }
        }
    }


    override fun handleState(state: DashBoardDataUiState) =
        when (state) {
            is DashBoardDataUiState.DashBoardData -> {
                binding.dashboardDataResponse = state.response

                val pieChartData = ArrayList<PieEntry>().apply {
                    add(PieEntry(state.response.totalDiscount.toFloat() , "Descuento"))
                    add(PieEntry(state.response.totalRaised.toFloat(), "Recaudado"))
                    add(PieEntry(state.response.totalToCollect.toFloat() , "Pendiente"))
                }
                DashboardPieChart(binding.chart1, pieChartData).run { setData(pieChartData) }

            }

            is DashBoardDataUiState.CutServiceSuccess -> {
                showSuccessDialog(getString(R.string.cutt_off_iniated))
            }

            is DashBoardDataUiState.InvalidPasswordError -> showErrorDialog(state.error)
        }

    private fun showCutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar")
            .setMessage("¿Estas seguro de iniciar el corte?")
            .setPositiveButton("Si") { dialog, which ->
                val dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_cut_service, null)

                //show text input dialog
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirmar")
                    .setMessage("Debes verificar tu identidad")
                    .setView(dialogView)
                    .setPositiveButton("Si") { dialog, which ->
                        val editText = dialogView.findViewById<EditText>(R.id.et_cut_service)
                        val password = editText?.text.toString()
                        viewModel.startServiceCut(password)
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun generateCenterSpannableText(): SpannableString? {
        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.5f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.65f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return s
    }
}