package com.dscorp.ispadmin.presentation.di.app

import android.app.Application
import com.dscorp.ispadmin.presentation.di.modules.apiModule
import com.dscorp.ispadmin.presentation.di.modules.dialogFactoryModule
import com.dscorp.ispadmin.presentation.di.modules.repositoryModule
import com.dscorp.ispadmin.presentation.di.viewModelModule
import com.example.data2.data.di.BASE_URL
import com.example.data2.data.di.localDataModule
import com.example.data2.data.di.retrofitModule
import com.example.data2.data.repository.IRepository
import com.facebook.stetho.Stetho
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class KoinApplication : Application() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this);
        firebaseAnalytics = Firebase.analytics

        startKoin {
            androidContext(this@KoinApplication)
            allowOverride(true)
            modules(
                retrofitModule,
                apiModule,
                repositoryModule,
                viewModelModule,
                dialogFactoryModule,
                localDataModule,
                module {
                    single { firebaseAnalytics }
                }


            )

        }

        getKoin().run {
            setProperty(BASE_URL, com.example.data2.BuildConfig.BASE_URL)
        }

    }
}