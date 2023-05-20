package com.dscorp.ispadmin.presentation.ui.features.report

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.R
import com.dscorp.ispadmin.databinding.FragmentReportsBinding
import com.dscorp.ispadmin.presentation.extension.getDownloadedFileUri
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportsFragment : BaseFragment<ReportsUiState, FragmentReportsBinding>() {

    override val binding by lazy { FragmentReportsBinding.inflate(layoutInflater) }

    override val viewModel: ReportsViewModel by viewModel()
    override fun handleState(state: ReportsUiState) =
        when (state) {
            is ReportsUiState.DocumentReady -> downloadFile(state.document)
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
            DownloadDocumentType.DEBTORS_CUT_OFF_CANDIDATES -> viewModel.downloadDebtorsCutOffCandidatesSubscriptionsDocument()
        }

    override fun onViewReady(savedInstanceState: Bundle?) {

        binding.btnGetDebtorsCutOffCandidates.setOnClickListener {
            downloadDocument(DownloadDocumentType.DEBTORS_CUT_OFF_CANDIDATES)
        }

        binding.btnGetDebtorsCustomers.setOnClickListener {
//            downloadDocumentType = DownloadDocumentType.DEBTORS
            downloadDocument(DownloadDocumentType.DEBTORS)
//            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnGetWithPaymentCommitmentCusstomers.setOnClickListener {
//            downloadDocumentType = DownloadDocumentType.WITH_PAYMENT_COMMITMENT
            downloadDocument(DownloadDocumentType.WITH_PAYMENT_COMMITMENT)

//            requestExternalStoragePermissionAndDownloadDocument()
        }

        binding.btnSuspendedCustomers.setOnClickListener {
//            downloadDocumentType = DownloadDocumentType.SUSPENDED
//            requestExternalStoragePermissionAndDownloadDocument()
            downloadDocument(DownloadDocumentType.SUSPENDED)

        }

        binding.btnGetCutoffCustomers.setOnClickListener {
//            downloadDocumentType = DownloadDocumentType.CUT_OFF
//            requestExternalStoragePermissionAndDownloadDocument()
            downloadDocument(DownloadDocumentType.CUT_OFF)

        }

        binding.btnGetPastMonthDebtorCustomers.setOnClickListener {
//            downloadDocumentType = DownloadDocumentType.DEBTORS_FROM_PAST_MONTH
//            requestExternalStoragePermissionAndDownloadDocument()
            downloadDocument(DownloadDocumentType.DEBTORS_FROM_PAST_MONTH)

        }
    }


    private fun downloadFile(document: DownloadDocumentResponse) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                12
            )
        } else {
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
        DEBTORS_CUT_OFF_CANDIDATES,
        DEBTORS,
        WITH_PAYMENT_COMMITMENT,
        SUSPENDED,
        CUT_OFF,
        DEBTORS_FROM_PAST_MONTH
    }
}
