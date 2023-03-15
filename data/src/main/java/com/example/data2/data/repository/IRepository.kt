package com.example.data2.data.repository

import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import kotlinx.coroutines.Job

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
    suspend fun clearUserSession()
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
    suspend fun findSubscription(id: String): List<SubscriptionResponse>
    suspend fun registerPayment(payment: Payment): Payment
    suspend fun getNetworkDeviceTypes(): List<String>
    suspend fun getDebtors(): List<SubscriptionResponse>
    suspend fun registerIpPool(ipPool: IpPoolRequest): IpPool
    suspend fun getIpPoolList(): List<IpPool>
    suspend fun getRecentPaymentsHistory(idSubscription: Int, itemsLimit: Int): List<Payment>
    suspend fun getCoreDevices(): List<NetworkDevice>
    suspend fun editSubscription(subscription: UpdateSubscriptionPlanBody): SubscriptionResponse
    suspend fun downloadDebtorsDocument(): DownloadDocumentResponse
    suspend fun getDashBoardData(): DashBoardDataResponse
    suspend fun startServicetCut()
    suspend fun getHostDevices(): List<NetworkDevice>
    suspend fun getIpList(poolId: Int): List<Ip>
    suspend fun getCpeDevices(): List<NetworkDevice>
    suspend fun editNapBox(napBox: NapBox): NapBoxResponse
    suspend fun editServiceOrder(serviceOrder: ServiceOrder): ServiceOrderResponse
    suspend fun getMufas(): List<Mufa>
    suspend fun getUnconfirmedOnus(): List<Onu>
    suspend fun applyCoupon(code: String):Coupon?

}