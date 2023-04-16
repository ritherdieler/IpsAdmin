package com.dscorp.ispadmin.presentation.extension

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.util.Base64
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.dscorp.ispadmin.BuildConfig
import com.dscorp.ispadmin.CrossDialogFragment
import com.dscorp.ispadmin.presentation.util.IDialogFactory
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import com.example.cleanarchitecture.domain.domain.entity.GeoLocation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

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

fun Fragment.showLoadingFullScreen() {

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

fun <T> MaterialAutoCompleteTextView.populate(data: List<T>, onItemSelected: (T) -> Unit) {
    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, data)
    setAdapter(adapter)
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, pos, _ ->
            onItemSelected(data[pos])
        }
}


fun Fragment.showCrossDialog(
    text: String? = null,
    closeButtonClickListener: (() -> Unit)? = null,
    positiveButtonClickListener: (() -> Unit)? = null
) {
    CrossDialogFragment(
        text = text,
        onCloseButtonClick = closeButtonClickListener ?: positiveButtonClickListener,
        onPositiveButtonClick = positiveButtonClickListener
    ).apply { isCancelable = false }.show(
        childFragmentManager,
        CrossDialogFragment::class.simpleName
    )
}

fun AppCompatActivity.showCrossDialog(
    text: String? = null,
    positiveButtonClickListener: (() -> Unit)? = null
) {
    CrossDialogFragment(text, positiveButtonClickListener).show(
        supportFragmentManager,
        CrossDialogFragment::class.simpleName
    )
}


@SuppressLint("MissingPermission")
fun FusedLocationProviderClient.getCurrentLocation(onLocation: (LatLng) -> Unit) {
    this.requestLocationUpdates(
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .apply {
                setWaitForAccurateLocation(true)
                setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(1000)
            }.build(),

        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let {
                    onLocation(LatLng(it.latitude, location.longitude))
                    removeLocationUpdates(this)
                }
            }
        },
        Looper.getMainLooper()
    )
}

fun Fragment.checkGpsEnabled(onGpsEnabled: () -> Unit) {
    if ((requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    ) {
        onGpsEnabled.invoke()
    } else {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            })
        val client: SettingsClient =
            LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is enabled, so you can perform your location-related tasks here
            onGpsEnabled.invoke()
        }.addOnFailureListener { exception ->
            when ((exception as ResolvableApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    exception.startResolutionForResult(requireActivity(), 1)
                }
            }
        }
    }
}

fun ImageView.animateRotate360InLoop() {
    startAnimation(
        RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
        }
    )
}

fun String.encryptWithSHA384(): String {
    val bytes = MessageDigest
        .getInstance("SHA-384")
        .digest(this.toByteArray(StandardCharsets.UTF_8))
    return bytes.fold("") { str, it -> str + "%02x".format(it) }
}

fun Calendar.isSameMonthAndYear(calendar: Calendar) =
    get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && get(Calendar.MONTH) == calendar.get(
        Calendar.MONTH
    )

fun Calendar.isLastDayOfMonth() = get(Calendar.DAY_OF_MONTH) == getActualMaximum(Calendar.DAY_OF_MONTH)

fun Long.asCalendar() = Calendar.getInstance().apply { timeInMillis = this@asCalendar }