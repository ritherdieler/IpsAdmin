package com.dscorp.ispadmin.data.local

import android.content.Context
import com.dscorp.ispadmin.domain.storage.FacadePhotoStorage
import java.io.File

class PendingFacadePhotoStorage(private val context: Context) : FacadePhotoStorage {

    override fun save(localId: String, source: File): File {
        val destination = fileFor(localId)
        source.copyTo(destination, overwrite = true)
        return destination
    }

    override fun fileFor(localId: String): File {
        return File(directory(), "$localId.jpg")
    }

    override fun delete(localId: String): Boolean {
        val file = fileFor(localId)
        return !file.exists() || file.delete()
    }

    private fun directory(): File {
        return File(context.filesDir, DIRECTORY_NAME).also { directory ->
            if (!directory.exists()) {
                directory.mkdirs()
            }
        }
    }

    companion object {
        const val DIRECTORY_NAME = "pending_subscriptions"
    }
}
