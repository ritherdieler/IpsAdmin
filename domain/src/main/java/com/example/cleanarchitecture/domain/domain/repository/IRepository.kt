package com.example.cleanarchitecture.domain.domain.repository

import com.example.cleanarchitecture.domain.domain.entity.*

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
    suspend fun getplans(): List<Plan>
    suspend fun getDevices(): List<NetworkDevice>
    suspend fun getSubscriptions(): List<SubscriptionResponse>
    suspend fun registerPlace(registerPlace: Place): Place
    suspend fun getPlaces():List<Place>
    suspend fun registerTechnician(registerTechnician: Technician): Technician
    suspend fun registerNapBox(napBox: NapBox): NapBox
    suspend fun registerServiceOrder(serviceOrder: ServiceOrder): ServiceOrder
    suspend fun getServicesOrder():List<ServiceOrder>
    suspend fun getTechnicians():List<Technician>
    suspend fun getNapBoxes():List<NapBox>
}