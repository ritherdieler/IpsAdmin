package com.dscorp.ispadmin

import android.app.Application
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.di.apiModule
import com.dscorp.ispadmin.presentation.di.repositoryModule
import com.dscorp.ispadmin.presentation.di.viewModelModule
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.example.data2.data.di.provideRetrofit
import com.example.data2.data.di.retrofitModule
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*

/**
 * Created by Sergio Carrillo Diestra on 25/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 */
class KoinAppForInstrumentation : Application() {
    private val FAKE_BASE_URL = "http://127.0.0.1:8080"

    override fun onCreate() = runTest {
        super.onCreate()


        val repository = spy(IRepository::class.java)


        launch {
            startKoin {
                modules(
                    listOf(
                        retrofitModule,
                        viewModelModule,
                        apiModule,
                        module {
                            single<IRepository> { repository }
                        }
                    )
                )
            }


        }


    }


    internal fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }

}