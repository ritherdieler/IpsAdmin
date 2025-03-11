package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetAvailableOnuListUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetPlanListUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetAvailableOnuListUseCase(get()) }
    single { GetPlanListUseCase(get()) }
}