package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.ui.features.dashboard.DashBoardViewModel
import com.dscorp.ispadmin.presentation.ui.features.ipPool.register.IpPoolViewModel
import com.dscorp.ispadmin.presentation.ui.features.login.LoginViewModel
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivityViewModel
import com.dscorp.ispadmin.presentation.ui.features.napbox.NapBoxViewModel
import com.dscorp.ispadmin.presentation.ui.features.napboxeslist.NapBoxesListViewModel
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.NetworkDeviceViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.register.RegisterPaymentViewModel
import com.dscorp.ispadmin.presentation.ui.features.place.PlaceViewModel
import com.dscorp.ispadmin.presentation.ui.features.plan.PlanViewModel
import com.dscorp.ispadmin.presentation.ui.features.profile.MyProfileViewmodel
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterViewModel
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsViewModel
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.history.ServicesOrderListViewModel
import com.dscorp.ispadmin.presentation.ui.features.serviceorder.register.RegisterServiceOrderViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.debtors.DebtorsViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.FindSubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.presentation.ui.features.technician.TechnicianViewModel
import com.dscorp.ispadmin.presentation.ui.features.technicianslist.TechniciansListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Sergio Carrillo Diestra on 20/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
val viewModelModule = module {
    viewModel { LoginViewModel() }
    viewModel { NetworkDeviceViewModel() }
    viewModel { PlaceViewModel() }
    viewModel { PlanViewModel() }
    viewModel { RegisterViewModel() }
    viewModel { SubscriptionViewModel(get()) }
    viewModel { SubscriptionsListViewModel() }
    viewModel { TechnicianViewModel() }
    viewModel { NapBoxViewModel() }
    viewModel { RegisterServiceOrderViewModel() }
    viewModel { ServicesOrderListViewModel() }
    viewModel { TechniciansListViewModel() }
    viewModel { NapBoxesListViewModel() }
    viewModel { PaymentHistoryViewModel() }
    viewModel { FindSubscriptionViewModel() }
    viewModel { RegisterPaymentViewModel(get()) }
    viewModel { DebtorsViewModel() }
    viewModel { IpPoolViewModel() }
    viewModel { MyProfileViewmodel() }
    viewModel { MainActivityViewModel(get()) }
    viewModel { ReportsViewModel(get()) }
    viewModel { DashBoardViewModel() }

}
