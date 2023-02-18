package com.example.data2.data.api

import com.example.cleanarchitecture.domain.domain.entity.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
interface RestApiServices {

    @POST("user")
    suspend fun registerUser(@Body user: User): Response<User>

    @POST("login")
    suspend fun doLoging(@Body loging: Loging): Response<User>

    @POST("plan")
    suspend fun registerPlan(@Body plan: Plan): Response<Plan>

    @POST("networkDevice")
    suspend fun registerNetworkDevice(@Body networkDevice: NetworkDevice): Response<NetworkDevice>

    @POST("subscription")
    suspend fun registerSubscription(@Body subscription: Subscription): Response<Subscription>

    @GET("plan")
    suspend fun getPlans(): Response<List<Plan>>

    @GET("networkDevice")
    suspend fun getDevices(): Response<List<NetworkDevice>>

    @GET("subscription")
    suspend fun getSubscriptions(): Response<List<SubscriptionResponse>>

    @POST("place")
    suspend fun registerPlace(@Body place: Place): Response<Place>

    @GET("place")
    suspend fun getPlaces(): Response<List<Place>>

    @POST("technician")
    suspend fun registerTechnician(@Body technician: Technician): Response<Technician>

    @POST("napbox")
    suspend fun registerNapBox(@Body napBox: NapBox): Response<NapBox>

    @POST("service-order")
    suspend fun registerServiceOrder(@Body serviceOrder: ServiceOrder): Response<ServiceOrder>

    @GET("service-order")
    suspend fun getServicesOrder(): Response<List<ServiceOrderResponse>>

    @GET("technician")
    suspend fun getTechnicians(): Response<List<Technician>>

    @GET("napbox")
    suspend fun getNapBoxes(): Response<List<NapBox>>

    @GET("payment/filtered")
    suspend fun getFilteredPaymentHistory(
        @Query("subscriptionId") subscriptionCode: Int,
        @Query("startDate") startDate: Long?,
        @Query("endDate") endDate: Long?
    ): Response<List<Payment>>

    @GET("subscription/find")
    suspend fun findSubscription(@Query("id") id: Int): Response<List<SubscriptionResponse>>

    @POST("payment")
    suspend fun registerPayment(@Body payment: Payment): Response<Payment>

    @GET("networkDevice/deviceTypes")
    suspend fun getNetworkDeviceTypes(): Response<List<String>>

    @GET("subscription/debtors")
    suspend fun getDebtors(): Response<List<SubscriptionResponse>>

    @POST("ip-pool")
    suspend fun registerIpPool(@Body ipPool: IpPool): Response<IpPool>

    @GET("ip-pool")
    suspend fun getIpPoolList(): Response<List<IpPool>>

    @GET("payment")
    suspend fun getRecentPaymentsHistory(
        @Query("subscriptionId") idSubscription: Int,
        @Query("limit") itemsLimit: Int
    ): Response<List<Payment>>

    @GET("networkDevice/coreTypes")
   suspend fun getCoreDevices(): Response<List<NetworkDevice>>

   @PUT("subscription")
    suspend fun editSubscription(@Body subscription: Subscription):Response<SubscriptionResponse>

    @GET("subscription/debtors-document")
    suspend fun downloadDebtorsDocument(): Response<DownloadDocumentResponse>
}

