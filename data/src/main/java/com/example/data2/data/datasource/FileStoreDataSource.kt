package com.example.data2.data.datasource

import android.net.Uri

interface FileStoreDataSource {

    suspend fun uploadImage(imageUri: Uri)
    suspend fun deleteImage(imageUrl: String)

}