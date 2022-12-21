package com.dscorp.ispadmin.repository

import com.dscorp.ispadmin.repository.model.*

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class Repository(val restApiServices: RestApiServices) : IRepository {

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

    override suspend fun registerPlace(registerPlace: IdAbbreviation): IdAbbreviation {
        val response = restApiServices.registerplace(registerPlace)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("ERROR")
        }

    }

}
