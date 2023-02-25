package com.dscorp.ispadmin.presentation.extension

import android.R
import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.dscorp.ispadmin.BuildConfig
import com.dscorp.ispadmin.presentation.util.IDialogFactory
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

fun NavController.navigateSafe(destinationId: Int) {
    try {
        navigate(destinationId)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun LatLng.toGeoLocation(): GeoLocation = GeoLocation(latitude, longitude)
fun LatLng.toStringLocation(): String = "$latitude, $longitude"

fun MaterialAutoCompleteTextView.fillWithList(data: List<Any>, onItemSelected: (Any) -> Unit) {
    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, data)
    setAdapter(adapter)
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, pos, _ ->
            onItemSelected(data[pos])
        }
}

fun Fragment.showSuccessDialog(text: String) {
    val dialogFactory: IDialogFactory by inject()
    val successDialog = dialogFactory.createSuccessDialog(requireContext(), text)
    successDialog.show()
}

fun Fragment.showErrorDialog(error: String? = "Error desconocido") {
    val dialogFactory: IDialogFactory by inject()
    val errorDialog = dialogFactory.createErrorDialog(requireContext(), error!!)
    errorDialog.show()
}

fun Activity.showSuccessDialog(text: String) {
    val dialogFactory: IDialogFactory by inject()
    val successDialog = dialogFactory.createSuccessDialog(this, text)
    successDialog.show()
}

fun Activity.showErrorDialog(error: String = "error desconocido") {
    val dialogFactory: IDialogFactory by inject()
    val errorDialog = dialogFactory.createErrorDialog(this, error)
    errorDialog.show()
}

fun Activity.getDownloadedFileUri(document: DownloadDocumentResponse): Uri {
    val data = Base64.decode(document.base64, Base64.DEFAULT)
    val downloadDir: File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val outputFile = File(downloadDir, document.getNameWithExtension())
    val outputStream = FileOutputStream(outputFile)
    outputStream.write(data)
    outputStream.close()

    return FileProvider.getUriForFile(
        this,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        outputFile
    )
}

fun <T> MaterialAutoCompleteTextView.fill(data: List<T>, onItemSelected: (T) -> Unit) {
    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, data)
    setAdapter(adapter)
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, pos, _ ->
            onItemSelected(data[pos])
        }
}
