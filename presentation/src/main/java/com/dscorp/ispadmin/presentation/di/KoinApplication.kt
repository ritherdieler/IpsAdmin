package com.dscorp.ispadmin.presentation.di

import android.app.Application
import com.dscorp.ispadmin.BuildConfig
import com.example.data2.data.di.BASE_URL
import com.example.data2.data.di.retrofitModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class KoinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this);

        startKoin {
            allowOverride(true)
            modules(
                retrofitModule,
                apiModule,
                repositoryModule,
                viewModelModule
            )

        }

        getKoin().run {
            setProperty(BASE_URL, com.example.data2.BuildConfig.BASE_URL)
        }

    }
}