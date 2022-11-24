package com.dscorp.ispadmin.repository

import com.dscorp.ispadmin.repository.model.Loging
import com.dscorp.ispadmin.repository.model.Plan
import com.dscorp.ispadmin.repository.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
interface RestApiServices {

    @POST("user")
    suspend fun registerUser(@Body user: User): Response <User>

    @POST("login")
    suspend fun doLoging(@Body loging : Loging): Response <User>

    @POST("plan")
    suspend fun registerPlan(@Body plan: Plan):Response <Plan>



}