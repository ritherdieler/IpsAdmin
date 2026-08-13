package com.dscorp.ispadmin.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PendingFacadePhotoStorageTest {

    private lateinit var context: Context
    private lateinit var storage: PendingFacadePhotoStorage

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        storage = PendingFacadePhotoStorage(context)
    }

    @Test
    fun `save persists photo at filesDir pending_subscriptions localId jpg`() {
        val localId = "local-123"
        val source = File.createTempFile("source", ".jpg").apply {
            writeBytes(byteArrayOf(1, 2, 3, 4))
        }

        val saved = storage.save(localId, source)

        val expected = File(File(context.filesDir, "pending_subscriptions"), "$localId.jpg")
        assertEquals(expected.absolutePath, saved.absolutePath)
        assertTrue(saved.exists())
        assertArrayEquals(byteArrayOf(1, 2, 3, 4), saved.readBytes())
        source.delete()
    }

    @Test
    fun `saved photo survives storage recreation`() {
        val localId = "local-restart"
        val source = File.createTempFile("source", ".jpg").apply {
            writeText("photo-bytes")
        }
        storage.save(localId, source)

        val storageAfterRestart = PendingFacadePhotoStorage(context)
        val restored = storageAfterRestart.fileFor(localId)

        assertTrue(restored.exists())
        assertEquals("photo-bytes", restored.readText())
        source.delete()
    }

    @Test
    fun `delete removes persisted photo`() {
        val localId = "local-delete"
        val source = File.createTempFile("source", ".jpg").apply {
            writeText("x")
        }
        storage.save(localId, source)

        storage.delete(localId)

        assertFalse(storage.fileFor(localId).exists())
        source.delete()
    }
}
