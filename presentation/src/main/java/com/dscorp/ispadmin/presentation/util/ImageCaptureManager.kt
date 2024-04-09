package com.dscorp.ispadmin.presentation.util

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

class ImageCaptureManager(
    private val lifecycleOwner: Fragment,
    private val onImageCaptured: (Uri) -> Unit
) {
    //
    private var tmpUri: Uri? = null
    private val takeImageResult =
        lifecycleOwner.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                tmpUri?.let { onImageCaptured(it) }
            }
        }

    fun takeImage() {
        lifecycleOwner.lifecycleScope.launch {
            getTmpFileUri().let { uri ->
                tmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_image_file", ".jpg", lifecycleOwner.requireContext().cacheDir)
                .apply {
                    createNewFile()
                    deleteOnExit()
                }

        return FileProvider.getUriForFile(
            lifecycleOwner.requireContext(),
            "com.dscorp.ispadmin.fileprovider",
            tmpFile
        )
    }
}