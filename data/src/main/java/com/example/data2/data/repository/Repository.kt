package com.example.data2.data.repository

import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.api.RestApiServices
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
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

            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
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

    override suspend fun getDevices(): List<NetworkDevice> {
        val response = restApiServices.getDevices()
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

    override suspend fun getServicesOrder(): List<ServiceOrder> {
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

    override suspend fun getPaymentHistory(request: SearchPaymentsRequest): List<Payment> {
        val response = restApiServices.getPaymentHistory(
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

    override suspend fun findSubscription(dni: String): List<SubscriptionResponse> {
        val response = restApiServices.findSubscription(dni)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
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


}
