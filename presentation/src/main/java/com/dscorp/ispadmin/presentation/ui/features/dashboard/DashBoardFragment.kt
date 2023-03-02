package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentDashBoardBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.extension.showSuccessDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashBoardFragment : BaseFragment() {
    private lateinit var loadingDialog:ProgressDialog
    val binding by lazy { FragmentDashBoardBinding.inflate(layoutInflater) }
    private val viewModel: DashBoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog.setMessage("Cargando saul")
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        viewModel.getDashBoardData()

        binding.btnStartCut.setOnClickListener {

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
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DashBoardDataUiState.DashBoardData -> {binding.dashboardDataResponse = state.response
                    loadingDialog.dismiss()
                }
                is DashBoardDataUiState.DashBoardDataError -> { showErrorDialog(state.message)
                    loadingDialog.dismiss()
                }

                is DashBoardDataUiState.CutServiceError -> { showErrorDialog(state.error)
                    loadingDialog.dismiss()
                }
                is DashBoardDataUiState.CutServiceSuccess -> {
                    showSuccessDialog("Se ha iniciado el corte correctamente")
                    loadingDialog.dismiss()
                }
            }
        }

        return binding.root
    }
}