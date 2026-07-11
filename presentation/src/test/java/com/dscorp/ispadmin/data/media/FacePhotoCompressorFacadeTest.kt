package com.dscorp.ispadmin.data.media

import android.app.Application
import android.graphics.Bitmap
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class FacePhotoCompressorFacadeTest {

    @Test
    fun `large facade photo is compressed below target size`() {
        val settings = FaceCaptureConfig.settings(FaceCaptureProfile.FACADE)
        val file = createLargeJpegFile(width = 4000, height = 3000)

        assertTrue(
            "El archivo de prueba debe superar el target antes de comprimir",
            file.length() > settings.targetSizeBytes,
        )

        FacePhotoCompressor.compressPhotoForBackend(file, FaceCaptureProfile.FACADE)

        assertTrue(
            "El archivo comprimido (${file.length()} bytes) debe quedar por debajo del target (${settings.targetSizeBytes} bytes)",
            file.length() <= settings.targetSizeBytes,
        )

        file.delete()
    }

    private fun createLargeJpegFile(width: Int, height: Int): File {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val random = Random(42)
        val pixels = IntArray(width * height) { random.nextInt() }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        val file = File.createTempFile("facade_photo_test_", ".jpg")
        file.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        }
        bitmap.recycle()
        return file
    }
}
