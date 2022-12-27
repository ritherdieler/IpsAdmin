package com.dscorp.ispadmin.repository

import com.dscorp.ispadmin.repository.model.*
import retrofit2.Response
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

    @POST("networkDevice")
    suspend fun registerNetworkDevice(@Body networkDevice: NetworkDevice):Response <NetworkDevice>

    @POST("subscription")
    suspend fun doSubscription(@Body subscription: Subscription): Response <Subscription>
    @GET("plan")
   suspend fun getPlans(): Response <List<Plan>>
   @GET("networkDevice")
   suspend fun getDevices(): Response <List<NetworkDevice>>
   @GET("subscription")
   suspend fun getSubscriptions():Response<List<Subscription>>
   @POST("place")
   suspend fun registerplace(@Body place:Place):Response<Place>
   @GET("place")
   suspend fun getPlaces():Response <List<Place>>


}
