package com.dscorp.ispadmin.repository

import com.dscorp.ispadmin.repository.model.Loging
import com.dscorp.ispadmin.repository.model.NetworkDevice
import com.dscorp.ispadmin.repository.model.Plan
import com.dscorp.ispadmin.repository.model.User

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class Repository(val restApiServices: RestApiServices) {

    suspend fun registerUser(user: User): User {
        val response = restApiServices.registerUser(user)
        if (response.code() == 200) {

            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }

    }


    suspend fun doLogin(login: Loging): User {
        val response = restApiServices.doLoging(login)
        if (response.code() == 200) {

            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }


    }

    suspend fun registerPlan(plan: Plan): Plan {
        val response = restApiServices.registerPlan(plan)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }
    }

    suspend fun registerNetworkDevice(registerNetworkDevice: NetworkDevice): NetworkDevice {
        val response = restApiServices.registerNetworkDevice(registerNetworkDevice)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("error en la respuesta")
        }


    }
}