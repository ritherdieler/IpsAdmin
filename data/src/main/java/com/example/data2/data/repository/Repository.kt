package com.example.data2.data.repository

import android.content.SharedPreferences
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.api.RestApiServices
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.utils.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class Repository : IRepository, KoinComponent {

    private val restApiServices: RestApiServices by inject()
    private val prefs: SharedPreferences by inject()

    override suspend fun registerUser(user: User): User {
        val response = restApiServices.registerUser(user)
        if (response.code() == 200) {

            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    override suspend fun doLogin(login: Loging): User {
        val response = restApiServices.doLoging(login)
        if (response.code() == 200) {
            val userSession = response.body()!!
            userSession.apply { password = login.password }
            saveUserSession(userSession, login.checkBox)
            return response.body()!!
        } else {
            throw Exception("Usuario o Contraseña Incorrecta")
        }
    }

    override suspend fun saveUserSession(user: User, rememberSessionCheckBoxStatus: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(REMEMBER_CHECKBOX_STATUS, rememberSessionCheckBoxStatus)
        editor.putString(SESSION_NAME, user.name)
        editor.putString(SESSION_LAST_NAME, user.lastName)
        editor.putString(SESSION_USER_NAME, user.username)
        editor.putString(SESSION_PASSWORD, user.password)
        editor.putInt(SESSION_TYPE, user.type)
        user.id?.let { editor.putInt(SESSION_ID, it) }
        editor.putBoolean(SESSION_VERIFIED, user.verified)
        editor.apply()
    }

    override fun getUserSession(): User? {
        val rememberCheckBoxStatus = prefs.getBoolean(REMEMBER_CHECKBOX_STATUS, false)
        return if (rememberCheckBoxStatus) {
            User(
                id = prefs.getInt(SESSION_ID, 0),
                name = prefs.getString(SESSION_NAME, "")!!,
                lastName = prefs.getString(SESSION_LAST_NAME, "")!!,
                type = prefs.getInt(SESSION_TYPE, 0),
                username = prefs.getString(SESSION_USER_NAME, "")!!,
                password = prefs.getString(SESSION_PASSWORD, "")!!,
                verified = prefs.getBoolean(SESSION_VERIFIED, false),
            )
        } else {
            null
        }
    }

    override suspend fun saveCheckBox(login: Loging) {
        val editor = prefs.edit()
        editor.putBoolean("checkbox", login.checkBox)
        editor.apply()
    }

    override suspend fun clearUserSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    override suspend fun registerPlan(plan: Plan): Plan {
        val response = restApiServices.registerPlan(plan)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    override suspend fun registerNetworkDevice(registerNetworkDevice: NetworkDevice): NetworkDevice {
        val response = restApiServices.registerNetworkDevice(registerNetworkDevice)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    override suspend fun doSubscription(doSubscription: Subscription): Subscription {
        val response = restApiServices.registerSubscription(doSubscription)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }

    }

    override suspend fun getPlans(): List<Plan> {
        val response = restApiServices.getPlans()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    override suspend fun getGenericDevices(): List<NetworkDevice> {
        val response = restApiServices.getGenericDevices()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR")
        }
    }

    override suspend fun getSubscriptions(): List<SubscriptionResponse> {
        val response = restApiServices.getSubscriptions()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR EN LA RESPUESTA")
        }
    }

    override suspend fun registerPlace(registerPlace: Place): Place {
        val response = restApiServices.registerPlace(registerPlace)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR")
        }
    }

    override suspend fun getPlaces(): List<Place> {
        val response = restApiServices.getPlaces()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    override suspend fun registerTechnician(registerTechnician: Technician): Technician {
        val response = restApiServices.registerTechnician(registerTechnician)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error en el registro")
        }
    }

    override suspend fun registerNapBox(napBox: NapBox): NapBox {

        val response = restApiServices.registerNapBox(napBox)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR")
        }
    }

    override suspend fun registerServiceOrder(serviceOrder: ServiceOrder): ServiceOrder {
        val response = restApiServices.registerServiceOrder(serviceOrder)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR")
        }
    }

    override suspend fun getServicesOrder(): List<ServiceOrderResponse> {
        val response = restApiServices.getServicesOrder()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR EN LA RESPUESTA")
        }
    }

    override suspend fun getTechnicians(): List<Technician> {
        val response = restApiServices.getTechnicians()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getNapBoxes(): List<NapBox> {
        val response = restApiServices.getNapBoxes()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getFilteredPaymentHistory(request: SearchPaymentsRequest): List<Payment> {
        val response = restApiServices.getFilteredPaymentHistory(
            request.subscriptionId!!,
            request.startDate,
            request.endDate
        )
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun findSubscription(id: String): List<SubscriptionResponse> {
        val response = restApiServices.findSubscription(id)
        return when (response.code()) {
            200 -> response.body()!!.ifEmpty { listOf() }
            else -> throw Exception("Error")
        }
    }

    override suspend fun registerPayment(payment: Payment): Payment {
        val response = restApiServices.registerPayment(payment)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getNetworkDeviceTypes(): List<String> {
        val response = restApiServices.getNetworkDeviceTypes()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getDebtors(): List<SubscriptionResponse> {
        val response = restApiServices.getDebtors()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun registerIpPool(ipPool: IpPoolRequest): IpPool {
        val response = restApiServices.registerIpPool(ipPool)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getIpPoolList(): List<IpPool> {
        val response = restApiServices.getIpPoolList()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getRecentPaymentsHistory(
        idSubscription: Int,
        itemsLimit: Int
    ): List<Payment> {
        val response = restApiServices.getRecentPaymentsHistory(idSubscription, itemsLimit)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getCoreDevices(): List<NetworkDevice> {
        val response = restApiServices.getCoreDevices()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun editSubscription(subscription: Subscription): SubscriptionResponse {
        val response = restApiServices.editSubscription(subscription)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadDebtorsDocument(): DownloadDocumentResponse {
        val response = restApiServices.downloadDebtorsDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getDashBoardData(): DashBoardDataResponse {
        val response = restApiServices.getDashBoardData()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun startServicetCut() {
        val response = restApiServices.startServiceCut()
        if (response.code() != 200) {
            throw Exception("Error")
        }
    }

    override suspend fun getHostDevices(): List<NetworkDevice> {
        val response = restApiServices.getCoreDevices()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getIpList(poolId: Int): List<Ip> {
        val response = restApiServices.getIpList(poolId)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getCpeDevices(): List<NetworkDevice> {
        val response = restApiServices.getCpeDevices()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("No se pudieron obtener los equipos cpe")
        }
    }
}
