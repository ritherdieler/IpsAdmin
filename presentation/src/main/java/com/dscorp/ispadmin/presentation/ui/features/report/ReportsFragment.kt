package com.dscorp.ispadmin.presentation.ui.features.report

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentReportsBinding
import com.dscorp.ispadmin.presentation.extension.getDownloadedFileUri
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsUiState.CutOffSubscriptionsDocument
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsUiState.DebtorsSubscriptionsDocument
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsUiState.SuspendedSubscriptionsDocument
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsUiState.WithPaymentCommitmentSubscriptionsDocument
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportsFragment : BaseFragment<ReportsUiState, FragmentReportsBinding>() {

    override val binding by lazy { FragmentReportsBinding.inflate(layoutInflater) }

    override val viewModel: ReportsViewModel by viewModel()
    override fun handleState(state: ReportsUiState) =
        when (state) {
            is CutOffSubscriptionsDocument -> downloadFile(state.document)
            is DebtorsSubscriptionsDocument -> downloadFile(state.document)
            is SuspendedSubscriptionsDocument -> downloadFile(state.document)
            is WithPaymentCommitmentSubscriptionsDocument -> downloadFile(state.document)
            is ReportsUiState.PastMonthSubscriptionDocument -> downloadFile(state.document)
        }


    private var downloadDocumentType: DownloadDocumentType? = null
    private val requestPermissionManager =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                downloadDocumentType?.let {
                    downloadDocument(it)
                }
            } else {
                showErrorDialog(getString(R.string.permission_denied))
            }
        }

    private fun downloadDocument(it: DownloadDocumentType) =
        when (it) {
            DownloadDocumentType.DEBTORS -> viewModel.downloadDebtorSubscriptionsDocument()
            DownloadDocumentType.WITH_PAYMENT_COMMITMENT -> viewModel.downloadPaymentCommitmentSubscriptionsDocument()
            DownloadDocumentType.SUSPENDED -> viewModel.downloadSuspendedSubscriptionsDocument()
            DownloadDocumentType.CUT_OFF -> viewModel.downloadCutOffSubscriptionsDocument()
            DownloadDocumentType.DEBTORS_FROM_PAST_MONTH -> viewModel.downloadPastMonthDebtors()
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.btnGetDebtorsCustomers.setOnClickListener {
            downloadDocumentType = DownloadDocumentType.DEBTORS

            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnGetWithPaymentCommitmentCusstomers.setOnClickListener {
            downloadDocumentType = DownloadDocumentType.WITH_PAYMENT_COMMITMENT
            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnSuspendedCustomers.setOnClickListener {
            downloadDocumentType = DownloadDocumentType.SUSPENDED
            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnGetCutoffCustomers.setOnClickListener {
            downloadDocumentType = DownloadDocumentType.CUT_OFF
            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnGetPastMonthDebtorCustomers.setOnClickListener {
            downloadDocumentType = DownloadDocumentType.DEBTORS_FROM_PAST_MONTH
            requestExternalStoragePermissionAndDownloadDocument()
        }

        return binding.root
    }

    private fun requestExternalStoragePermissionAndDownloadDocument() {

        requestPermissionManager.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun downloadFile(document: DownloadDocumentResponse) {
        lifecycleScope.launch {
            val uri = requireActivity().getDownloadedFileUri(document)
            val intent = openWithXlsxApp(uri)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                showErrorDialog("No se pudo abrir el archivo, el archivo se encuentra en la carpeta de descargas")
            }
        }
    }

    private fun openWithXlsxApp(uri: Uri): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    private enum class DownloadDocumentType {
        DEBTORS,
        WITH_PAYMENT_COMMITMENT,
        SUSPENDED,
        CUT_OFF,
        DEBTORS_FROM_PAST_MONTH
    }
}
