package com.dscorp.ispadmin.data.media

import android.content.Context
import android.net.Uri
import java.io.File

fun prepareFacadePhotoFile(context: Context, uri: Uri): File {
    val file = File.createTempFile(
        "facade_photo_",
        ".jpg",
        context.cacheDir
    )

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } ?: throw IllegalArgumentException("No se pudo leer la foto de fachada")

    FacePhotoCompressor.compressPhotoForBackend(file, FaceCaptureProfile.FACADE)

    return file
}
