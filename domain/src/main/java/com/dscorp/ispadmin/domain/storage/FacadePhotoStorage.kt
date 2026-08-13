package com.dscorp.ispadmin.domain.storage

import java.io.File

interface FacadePhotoStorage {
    fun save(localId: String, source: File): File
    fun fileFor(localId: String): File
    fun delete(localId: String): Boolean
}
