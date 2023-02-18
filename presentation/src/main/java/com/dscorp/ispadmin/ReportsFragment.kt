package com.dscorp.ispadmin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dscorp.ispadmin.databinding.FragmentReportsBinding
import com.dscorp.ispadmin.presentation.extension.showErrorDialog
import com.dscorp.ispadmin.presentation.ui.features.base.BaseFragment
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream


class ReportsFragment : BaseFragment() {

    private val binding by lazy { FragmentReportsBinding.inflate(layoutInflater) }
    private val viewModel: ReportsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
            val data = Base64.decode(document.base64, Base64.DEFAULT)
            val downloadDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outputFile = File(downloadDir, document.getNameWithExtension())
            val outputStream = FileOutputStream(outputFile)
            outputStream.write(data)
            outputStream.close()

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                outputFile
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no activity is available to handle the intent
                Toast.makeText(requireContext(), "No app found to open XLSX files", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Handle other exceptions
                e.printStackTrace()
            }
        }
    }


}