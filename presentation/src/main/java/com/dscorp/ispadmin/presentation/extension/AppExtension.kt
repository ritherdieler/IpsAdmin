package com.dscorp.ispadmin.presentation.extension

import android.R
import android.app.Activity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.dscorp.ispadmin.presentation.util.IDialogFactory
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject


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

fun Fragment.showSuccessDialog(text:String){
    val dialogFactory : IDialogFactory by inject ()
    val successDialog = dialogFactory.createSuccessDialog(requireContext(),text)
    successDialog.show()
}
fun Fragment.showErrorDialog() {
    val dialogFactory:IDialogFactory by inject()
    val errorDialog = dialogFactory.createErrorDialog(requireContext())
    errorDialog.show()
}
fun Activity.showSuccessDialog(text:String){
    val dialogFactory : IDialogFactory by inject ()
    val successDialog = dialogFactory.createSuccessDialog(this,text)
    successDialog.show()
}
fun Activity.showErrorDialog() {
    val dialogFactory:IDialogFactory by inject()
    val errorDialog = dialogFactory.createErrorDialog(this)
    errorDialog.show()
}
fun String.isValidIpv4( ): Boolean {
    val pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"
    val ipv4Regex = Regex(pattern)
    return ipv4Regex.matches(this)
}
fun String.hasOnlyLetters(): Boolean {
    val pattern = "^[a-zA-Z]+\$"
    val stringRegex = Regex(pattern)
    return stringRegex.matches(this)
}




