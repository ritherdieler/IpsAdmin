package com.dscorp.ispadmin

import android.app.Application
import com.dscorp.ispadmin.mockdata.subscriptionListMock
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.example.data2.data.repository.IRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mockito.*

/**
 * Created by Sergio Carrillo Diestra on 25/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 */
class KoinAppForInstrumentation : Application() {

    override fun onCreate()  = runTest{
        super.onCreate()
        startKoin {
//            androidLogger()
//            androidContext(this@KoinAppForInstrumentation)
            val repo = mock(IRepository::class.java)
            launch {
                `when`(repo.getSubscriptions()).thenReturn(subscriptionListMock)
                modules(listOf(
                    module {
                        single { repo }
                    },
                    module {
                        viewModel { spy(SubscriptionsListViewModel::class.java) }
                    }

                ))

            }


        }
    }

    internal fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }

}