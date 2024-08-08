package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.ui.features.dashboard.DashBoardViewModel
import com.dscorp.ispadmin.presentation.ui.features.fixedCost.FixedCostViewModel
import com.dscorp.ispadmin.presentation.ui.features.ippool.register.IpPoolViewModel
import com.dscorp.ispadmin.presentation.ui.features.ippool.seeip.IpListViewModel
import com.dscorp.ispadmin.presentation.ui.features.login.LoginViewModel
import com.dscorp.ispadmin.presentation.ui.features.main.MainActivityViewModel
import com.dscorp.ispadmin.presentation.ui.features.migration.MigrationViewModel
import com.dscorp.ispadmin.presentation.ui.features.mufas.MufaViewModel
import com.dscorp.ispadmin.presentation.ui.features.napbox.NapBoxViewModel
import com.dscorp.ispadmin.presentation.ui.features.napboxeslist.NapBoxesListViewModel
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.NetworkDeviceViewModel
import com.dscorp.ispadmin.presentation.ui.features.networkdevice.networkdevicelist.NetworkDeviceListViewModel
import com.dscorp.ispadmin.presentation.ui.features.oltadministrator.OltAdministrationViewModel
import com.dscorp.ispadmin.presentation.ui.features.outlay.OutLayViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.history.PaymentHistoryViewModel
import com.dscorp.ispadmin.presentation.ui.features.payment.register.RegisterPaymentViewModel
import com.dscorp.ispadmin.presentation.ui.features.place.PlaceViewModel
import com.dscorp.ispadmin.presentation.ui.features.place.placelist.PlaceListViewModel
import com.dscorp.ispadmin.presentation.ui.features.plan.PlanViewModel
import com.dscorp.ispadmin.presentation.ui.features.plan.edit.EditPlanViewModel
import com.dscorp.ispadmin.presentation.ui.features.plan.planlist.PlanListViewModel
import com.dscorp.ispadmin.presentation.ui.features.profile.MyProfileViewModel
import com.dscorp.ispadmin.presentation.ui.features.registration.RegisterViewModel
import com.dscorp.ispadmin.presentation.ui.features.report.ReportsViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.edit.EditSubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscription.register.RegisterSubscriptionViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscriptiondetail.SubscriptionDetailViewModel
import com.dscorp.ispadmin.presentation.ui.features.subscriptionfinder.compose.SubscriptionFinderViewModel
import com.dscorp.ispadmin.presentation.ui.features.supportTicket.SupportTicketViewModel
import com.dscorp.ispadmin.presentation.ui.features.technician.TechnicianViewModel
import com.dscorp.ispadmin.presentation.ui.features.technician.technicianlist.TechnicianListViewModel
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
    viewModel { LoginViewModel(get()) }
    viewModel { NetworkDeviceViewModel() }
    viewModel { PlaceViewModel() }
    viewModel { PlanViewModel() }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { RegisterSubscriptionViewModel(get(), get()) }
    viewModel { TechnicianViewModel() }
    viewModel { NapBoxViewModel(get()) }
    viewModel { NapBoxesListViewModel() }
    viewModel { PaymentHistoryViewModel(get()) }
//    viewModel { FindSubscriptionViewModel(get(), get()) }
    viewModel { RegisterPaymentViewModel(get()) }
    viewModel { IpPoolViewModel(get()) }
    viewModel { MyProfileViewModel() }
    viewModel { MainActivityViewModel(get()) }
    viewModel { ReportsViewModel(get()) }
    viewModel { DashBoardViewModel() }
    viewModel { IpListViewModel(get()) }
    viewModel { PlanListViewModel(get()) }
    viewModel { NetworkDeviceListViewModel() }
    viewModel { PlaceListViewModel() }
    viewModel { TechnicianListViewModel() }
    viewModel { MufaViewModel(get()) }
    viewModel { EditSubscriptionViewModel(get()) }
    viewModel { EditPlanViewModel(get()) }
    viewModel { SubscriptionDetailViewModel(get(), get()) }
    viewModel { SupportTicketViewModel(get(), get()) }
    viewModel { MigrationViewModel(get()) }
    viewModel { OltAdministrationViewModel(get()) }
    viewModel { OutLayViewModel(get()) }
    viewModel { SubscriptionFinderViewModel(get()) }
    viewModel { FixedCostViewModel(get()) }
}
