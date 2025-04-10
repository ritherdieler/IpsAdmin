package com.example.data2.data.repository

import com.example.cleanarchitecture.domain.entity.AppVersion
import com.example.cleanarchitecture.domain.entity.Coupon
import com.example.cleanarchitecture.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.entity.DashBoardDataResponse
import com.example.cleanarchitecture.domain.entity.DownloadDocumentResponse
import com.example.cleanarchitecture.domain.entity.FireBaseResponse
import com.example.cleanarchitecture.domain.entity.FirebaseBody
import com.example.cleanarchitecture.domain.entity.FixedCost
import com.example.cleanarchitecture.domain.entity.Ip
import com.example.cleanarchitecture.domain.entity.IpPool
import com.example.cleanarchitecture.domain.entity.Loging
import com.example.cleanarchitecture.domain.entity.Mufa
import com.example.cleanarchitecture.domain.entity.NapBox
import com.example.cleanarchitecture.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.entity.NetworkDeviceResponse
import com.example.cleanarchitecture.domain.entity.Onu
import com.example.cleanarchitecture.domain.entity.Outlay
import com.example.cleanarchitecture.domain.entity.Payment
import com.example.cleanarchitecture.domain.entity.Place
import com.example.cleanarchitecture.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.entity.Plan
import com.example.cleanarchitecture.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.entity.ServiceOrderResponse
import com.example.cleanarchitecture.domain.entity.Subscription
import com.example.cleanarchitecture.domain.entity.SubscriptionFastSearchResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.entity.SubscriptionResume
import com.example.cleanarchitecture.domain.entity.Technician
import com.example.cleanarchitecture.domain.entity.User
import com.example.cleanarchitecture.domain.entity.extensions.PayerFinderResult
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.apirequestmodel.FixedCostRequest
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.MigrationRequest
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.apirequestmodel.UpdateSubscriptionDataBody
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.response.AdministrativeOnuResponse
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus
import java.io.File

/**
 * Created by Sergio Carrillo Diestra on 25/12/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
interface IRepository {
    suspend fun registerUser(user: User): User
    suspend fun doLogin(login: Loging): User
    suspend fun saveUserSession(user: User, rememberSessionCheckBoxStatus: Boolean?)
    fun getUserSession(): User?
    fun getRememberSessionCheckBoxStatus(): Boolean
    fun clearUserSession()
    suspend fun registerPlan(plan: Plan): Plan
    suspend fun registerNetworkDevice(registerNetworkDevice: NetworkDevice): NetworkDevice
    suspend fun getGenericDevices(): List<NetworkDevice>
    suspend fun registerSubscription(subscription: Subscription): Subscription
    suspend fun getPlans(): List<PlanResponse>
    suspend fun getDevices(): List<NetworkDeviceResponse>
    suspend fun getSubscriptions(): List<SubscriptionResponse>
    suspend fun registerPlace(registerPlace: Place): Place
    suspend fun getPlaces(): List<PlaceResponse>
    suspend fun registerTechnician(registerTechnician: Technician): Technician
    suspend fun registerNapBox(napBox: NapBox): NapBox
    suspend fun registerServiceOrder(serviceOrder: ServiceOrder): ServiceOrder
    suspend fun getServicesOrder(): List<ServiceOrderResponse>
    suspend fun getTechnicians(): List<Technician>
    suspend fun getNapBoxes(): List<NapBoxResponse>
    suspend fun getFilteredPaymentHistory(request: SearchPaymentsRequest): List<Payment>
    suspend fun findSubscriptionByDNI(id: String): List<SubscriptionResume>
    suspend fun registerPayment(payment: Payment): Payment
    suspend fun getNetworkDeviceTypes(): List<String>
    suspend fun getDebtors(): List<SubscriptionResponse>
    suspend fun registerIpPool(ipPool: IpPoolRequest): IpPool
    suspend fun getIpPoolList(): List<IpPool>
    suspend fun getRecentPaymentsHistory(idSubscription: Int, itemsLimit: Int): List<Payment>
    suspend fun getCoreDevices(): List<NetworkDevice>
    suspend fun updateSubscriptionPlan(subscription: UpdateSubscriptionPlanBody): SubscriptionResponse
    suspend fun downloadDebtorWithActiveSubscriptionsReport(): DownloadDocumentResponse

    suspend fun downloadPaymentCommitmentSubscriptionsReport(): DownloadDocumentResponse

    suspend fun downloadSuspendedSubscriptionsReport(): DownloadDocumentResponse

    suspend fun downloadCutOffSubscriptionsReport(): DownloadDocumentResponse

    suspend fun downloadPastMonthDebtorsReport(): DownloadDocumentResponse

    suspend fun getDashBoardData(): DashBoardDataResponse
    suspend fun startServiceCut()
    suspend fun getHostDevices(): List<NetworkDevice>
    suspend fun getIpList(poolId: Int): List<Ip>
    suspend fun getCpeDevices(): List<NetworkDevice>
    suspend fun editNapBox(napBox: NapBox): NapBoxResponse
    suspend fun editServiceOrder(serviceOrder: ServiceOrder): ServiceOrderResponse
    suspend fun getMufas(): List<Mufa>
    suspend fun  getUnconfirmedOnus(): List<Onu>
    suspend fun applyCoupon(code: String): Coupon?
    suspend fun findSubscriptionBySubscriptionDate(
        startDate: String,
        endDate: String
    ): List<SubscriptionResume>

    suspend fun sendCloudMessaging(body: FirebaseBody?): FireBaseResponse
    suspend fun updatePlan(plan: Plan): PlanResponse
    suspend fun savePaymentCommitment(id: Int)
    suspend fun reactivateService(subscription: Int, responsibleId: Int)
    suspend fun findSubscriptionByNameAndLastName(
        name: String?,
        lastName: String?
    ): List<SubscriptionResume>

    suspend fun downloadDebtorsCutOffCandidatesSubscriptionsReport(): DownloadDocumentResponse
    suspend fun cancelSubscription(subscriptionId: Int)
    suspend fun updateSubscriptionData(subscriptionData: UpdateSubscriptionDataBody)
    suspend fun downloadDebtorWithCancelledSubscriptionsReport(): DownloadDocumentResponse
    suspend fun downloadCancelledSubscriptionsFromCurrentMonthReport(): DownloadDocumentResponse
    suspend fun downloadCancelledSubscriptionsFromPastMonthReport(): DownloadDocumentResponse

    suspend fun getTicket(ticketId: String): AssistanceTicketResponse
    suspend fun getTicketsByStatus(pending: AssistanceTicketStatus): List<AssistanceTicketResponse>
    suspend fun assignSupportTicketToUser(
        id: Int,
        newStatus: AssistanceTicketStatus,
        userId: Int,
    ): AssistanceTicketResponse

    suspend fun closeTicket(
        id: Int,
        newStatus: AssistanceTicketStatus,
        userId: Int,
        imageBase64: File
    ): AssistanceTicketResponse

    suspend fun closeUnattendedTicket(
        id: Int,
        newStatus: AssistanceTicketStatus,
        userId: Int,
    ): AssistanceTicketResponse

    suspend fun createTicket(value: AssistanceTicketRequest): AssistanceTicketResponse
    suspend fun findSubscriptionByNames(names: String): List<SubscriptionFastSearchResponse>
    suspend fun doMigration(migrationRequest: MigrationRequest): SubscriptionResponse
    suspend fun getOnuBySn(s: String): AdministrativeOnuResponse
    suspend fun deleteOnuFromOlt(onuExternalId: String)
    suspend fun saveOutLay(apply: Outlay)
    suspend fun getElectronicPayers(subscriptionId: Int): List<String>
    suspend fun updateCustomerData(customer: CustomerData): Unit
    suspend fun subscriptionById(subscriptionId: Int): SubscriptionResponse
    suspend fun changeSubscriptionNapBox(request: MoveOnuRequest)
    suspend fun getRemoteAppVersion(): AppVersion
    suspend fun getTicketsByDateRange(
        closed: AssistanceTicketStatus,
        firstDayOfMonth: Long,
        lastDayOfMonth: Long
    ): List<AssistanceTicketResponse>
    suspend fun saveFixedCost(fixedCostRequest: FixedCostRequest)
    suspend fun getAllFixedCosts(): List<FixedCost>
    suspend fun findPaymentByElectronicPayerName(electronicPayerName: String): List<PayerFinderResult>
    suspend fun getPlaceFromLocation(latitude: Double, longitude: Double): PlaceResponse
}