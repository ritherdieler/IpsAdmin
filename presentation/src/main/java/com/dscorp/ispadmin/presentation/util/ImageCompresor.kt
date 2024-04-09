package com.dscorp.ispadmin.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min


fun File.compressImage(quality:Int): File {
    val bitmap = updateDecodeBounds(this)
    val fileOut = FileOutputStream(this)
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOut)
    fileOut.flush()
    fileOut.close()
    bitmap.recycle()
    return this
}

private fun updateDecodeBounds(imageFile: File): Bitmap {
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        val sampleHeight = if (outWidth > outHeight) 900 else 1100
        val sampleWidth = if (outWidth > outHeight) 1100 else 900
        inSampleSize = min(outWidth / sampleWidth, outHeight / sampleHeight)
        inJustDecodeBounds = false
        val decodedFile = BitmapFactory.decodeFile(imageFile.path, this)
        decodedFile
    }
}

fun rotateImageIfNeeded(context: Context, imageFile: File, imageUri: Uri): File? {
    return try {
        val exif = ExifInterface(context.contentResolver.openInputStream(imageUri)!!)
        val rotation = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        if (rotation != 0) rotateImage(imageFile, rotation) else imageFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun rotateImage(imageFile: File, rotation: Int): File? {
    return try {
        val bitmap = BitmapFactory.decodeFile(imageFile.path)
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        val out = FileOutputStream(imageFile)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        rotatedBitmap.recycle()
        imageFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
