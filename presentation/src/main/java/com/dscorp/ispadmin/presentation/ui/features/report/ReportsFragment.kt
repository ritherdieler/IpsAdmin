package com.dscorp.ispadmin.presentation.ui.features.report

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.FragmentReportsBinding
import com.dscorp.ispadmin.presentation.extension.getDownloadedFileUri
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ReportsFragment : BaseFragment() {

    private val binding by lazy { FragmentReportsBinding.inflate(layoutInflater) }
    private val viewModel: ReportsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.btnGetDebtors.setOnClickListener {
            viewModel.downloadDebtorsDocument()
        }

        observeUiState()

        return binding.root
    }

    private fun observeUiState() {
        viewModel.uiStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReportsUiState.DebtorsDocument -> downloadDocument(state.document)
                is ReportsUiState.DebtorsDocumentError -> showErrorDialog(state.error)
            }
        }
    }

    private fun downloadDocument(document: DownloadDocumentResponse) {
        lifecycleScope.launch(Dispatchers.IO) {
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
}
