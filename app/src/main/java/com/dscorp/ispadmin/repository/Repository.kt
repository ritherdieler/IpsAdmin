package com.dscorp.ispadmin.repository

import com.dscorp.ispadmin.repository.model.*
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
        val response = restApiServices.doSubscription(doSubscription)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }

    }

    override suspend fun getplans(): List<Plan> {
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

    override suspend fun getSubscriptions(): List<Subscription> {
        val response = restApiServices.getSubscriptions()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR EN LA RESPUESTA")
        }
    }

    override suspend fun registerPlace(registerPlace: Place): Place {
        val response = restApiServices.registerplace(registerPlace)
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
     override suspend fun registerTechnician(registerTechnician:Technician):Technician {
        val response = restApiServices.registerTechnician(registerTechnician)
        if (response.code() == 200) {
            return response.body()!!
        }else{
            throw Exception("Error en el registro")
        }
    }

}
