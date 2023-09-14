package com.dscorp.ispadmin.presentation.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment

fun Fragment.showCurrentSimpleName() {
    Log.w("CSN", (this::class.simpleName.toString()))
}

fun Activity.showCurrentSimpleName() {
    Log.w("CSN", (this::class.simpleName.toString()))
}

fun Fragment.openWithXlsxApp(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(
        uri,
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        showErrorDialog("No se pudo abrir el archivo, instale una aplicación para abrir archivos de excel")
    }
}
