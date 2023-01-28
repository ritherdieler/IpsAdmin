package com.dscorp.ispadmin.presentation.di

import com.dscorp.ispadmin.presentation.login.LoginViewModel
import com.dscorp.ispadmin.presentation.napbox.NapBoxViewModel
import com.dscorp.ispadmin.presentation.napboxeslist.NapBoxesListViewModel
import com.dscorp.ispadmin.presentation.networkdevice.NetworkDeviceViewModel
import com.dscorp.ispadmin.presentation.payment.consult.ConsultPaymentsViewModel
import com.dscorp.ispadmin.presentation.payment.register.RegisterPaymentViewModel
import com.dscorp.ispadmin.presentation.place.PlaceViewModel
import com.dscorp.ispadmin.presentation.plan.PlanViewModel
import com.dscorp.ispadmin.presentation.registration.RegisterViewModel
import com.dscorp.ispadmin.presentation.serviceorden.ServiceOrderViewModel
import com.dscorp.ispadmin.presentation.serviceorderlist.ServicesOrderListViewModel
import com.dscorp.ispadmin.presentation.subscription.SubscriptionViewModel
import com.dscorp.ispadmin.presentation.subscriptionfinder.FindSubscriptionViewModel
import com.dscorp.ispadmin.presentation.subscriptionlist.SubscriptionsListViewModel
import com.dscorp.ispadmin.presentation.technician.TechnicianViewModel
import com.dscorp.ispadmin.presentation.technicianslist.TechniciansListViewModel
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
    viewModel { SubscriptionViewModel() }
    viewModel { SubscriptionsListViewModel() }
    viewModel { TechnicianViewModel() }
    viewModel { NapBoxViewModel() }
    viewModel { ServiceOrderViewModel() }
    viewModel {ServicesOrderListViewModel()}
    viewModel {TechniciansListViewModel()}
    viewModel {NapBoxesListViewModel()}
    viewModel { ConsultPaymentsViewModel() }
    viewModel {FindSubscriptionViewModel()}
    viewModel { RegisterPaymentViewModel() }

}