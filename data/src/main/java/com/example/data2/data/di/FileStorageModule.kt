package com.example.data2.data.di

import com.example.data2.data.datasource.FileStoreDataSource
import com.example.data2.data.datasource.FileStoreDataSourceImpl
import com.example.data2.data.datasource.FirebaseStorageService
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val fileStorageModule = module {
    single<FirebaseStorageService> { providesFileStorageApi(get(named("storageRetrofit"))) }
    single<FileStoreDataSource> { provideFileStorageDataSource(get()) }
}

fun  providesFileStorageApi(retrofit: Retrofit ): FirebaseStorageService {
    return retrofit.create(FirebaseStorageService::class.java)
}

fun provideFileStorageDataSource(firebaseStorageService: FirebaseStorageService): FileStoreDataSource {
    return FileStoreDataSourceImpl(firebaseStorageService)
}
