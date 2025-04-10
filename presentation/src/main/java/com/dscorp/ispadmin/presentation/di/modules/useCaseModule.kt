package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetAvailableOnuListUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetNapBoxListUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetPlaceFromLocationUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetPlaceListUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetPlanListUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetCoreDevicesUseCase
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.compose.GetUserSessionUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetAvailableOnuListUseCase(get()) }
    single { GetPlanListUseCase(get()) }
    single { GetPlaceListUseCase(get()) }
    single { GetPlaceFromLocationUseCase(get()) }
    single { GetNapBoxListUseCase(get()) }
    single { RegisterSubscriptionUseCase(get()) }
    single { GetUserSessionUseCase(get()) }
    single { GetCoreDevicesUseCase(get()) }
}
