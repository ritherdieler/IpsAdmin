package com.example.data2.data.repository

import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest

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
    suspend fun registerPlan(plan: Plan): Plan
    suspend fun registerNetworkDevice(registerNetworkDevice: NetworkDevice): NetworkDevice
    suspend fun doSubscription(doSubscription: Subscription): Subscription
    suspend fun getPlans(): List<Plan>
    suspend fun getDevices(): List<NetworkDevice>
    suspend fun getSubscriptions(): List<SubscriptionResponse>
    suspend fun registerPlace(registerPlace: Place): Place
    suspend fun getPlaces():List<Place>
    suspend fun registerTechnician(registerTechnician: Technician): Technician
    suspend fun registerNapBox(napBox: NapBox): NapBox
    suspend fun registerServiceOrder(serviceOrder: ServiceOrder): ServiceOrder
    suspend fun getServicesOrder():List<ServiceOrderResponse>
    suspend fun getTechnicians():List<Technician>
    suspend fun getNapBoxes():List<NapBox>
    suspend fun getPaymentHistory(request:SearchPaymentsRequest):List<Payment>
    suspend fun findSubscription(id: Int): List<SubscriptionResponse>
    suspend fun registerPayment(payment: Payment): Payment
    suspend fun getNetworkDeviceTypes() : List<String>
    suspend fun getDebtors() : List<SubscriptionResponse>
}