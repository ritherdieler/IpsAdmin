package com.dscorp.ispadmin.presentation.extension

import android.R
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.NavController
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.MaterialAutoCompleteTextView


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
