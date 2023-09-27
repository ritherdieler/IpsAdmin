package com.example.data2.data.api

import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.UpdateSubscriptionDataBody
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus
import com.example.data2.data.response.BaseResponse
import retrofit2.Response
import retrofit2.http.*

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
    suspend fun registerSubscription(@Body subscription: Subscription): BaseResponse<Subscription>

    @GET("plan")
    suspend fun getPlans(): Response<List<PlanResponse>>

    @GET("networkDevice/genericDevices")
    suspend fun getGenericDevices(): Response<List<NetworkDevice>>

    @GET("networkDevice/fiber-and-wireless-devices")
    suspend fun getCpeDevices(): Response<List<NetworkDevice>>

    @GET("networkDevice")
    suspend fun getDevices(): Response<List<NetworkDeviceResponse>>

    @GET("subscription")
    suspend fun getSubscriptions(): Response<List<SubscriptionResponse>>

    @POST("place")
    suspend fun registerPlace(@Body place: Place): Response<Place>

    @GET("place")
    suspend fun getPlaces(): Response<List<PlaceResponse>>

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
    suspend fun getNapBoxes(): Response<List<NapBoxResponse>>

    @GET("payment/filtered")
    suspend fun getFilteredPaymentHistory(
        @Query("subscriptionId") subscriptionCode: Int,
        @Query("startDate") startDate: Long?,
        @Query("endDate") endDate: Long?
    ): Response<List<Payment>>

    @GET("subscription/find/dni")
    suspend fun findSubscriptionByDNI(@Query("dni") dni: String): Response<List<SubscriptionResponse>>

    @GET("subscription/find/date")
    suspend fun findSubscriptionBySubscriptionDate(
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long
    ): Response<List<SubscriptionResponse>>

    @PUT("payment")
    suspend fun registerPayment(@Body payment: Payment): Response<Payment>

    @GET("networkDevice/deviceTypes")
    suspend fun getNetworkDeviceTypes(): Response<List<String>>

    @GET("subscription/debtors")
    suspend fun getDebtors(): Response<List<SubscriptionResponse>>

    @POST("ip-pool")
    suspend fun registerIpPool(@Body ipPool: IpPoolRequest): Response<IpPool>

    @GET("ip-pool")
    suspend fun getIpPoolList(): Response<List<IpPool>>

    @GET("payment")
    suspend fun getRecentPaymentsHistory(
        @Query("subscriptionId") idSubscription: Int,
        @Query("limit") itemsLimit: Int
    ): Response<List<Payment>>

    @GET("networkDevice/coreTypes")
    suspend fun getCoreDevices(): Response<List<NetworkDevice>>

    @PUT("subscription/update-plan")
    suspend fun updateSubscriptionPlan(@Body subscription: UpdateSubscriptionPlanBody): Response<SubscriptionResponse>

    @GET("subscription/with-payment-commitment-report-document")
    suspend fun downloadWithPaymentCommitmentSubscriptionsReportDocument(): Response<DownloadDocumentResponse>

    @GET("subscription/suspended-report-document")
    suspend fun downloadSuspendedSubscriptionsReportDocument(): Response<DownloadDocumentResponse>

    @GET("subscription/cutoff-report-document")
    suspend fun downloadCutOffSubscriptionsReportDocument(): Response<DownloadDocumentResponse>

    @GET("subscription/last-month-debtors-report-document")
    suspend fun downloadPastMontDebtorsSubscriptionsReportDocument(): Response<DownloadDocumentResponse>

    @GET("dashboard")
    suspend fun getDashBoardData(): Response<DashBoardDataResponse>

    @PUT("subscription/cut-service")
    suspend fun startServiceCut(): Response<Any>

    @GET("ip-pool/get-ips")
    suspend fun getIpList(@Query("ipPoolId") poolId: Int): Response<List<Ip>>

    @PUT("napbox")
    suspend fun editNapBox(@Body napBox: NapBox): Response<NapBoxResponse>

    @PUT("service-order")
    suspend fun editServiceOrder(@Body serviceOrder: ServiceOrder): Response<ServiceOrderResponse>

    @GET("mufa")
    suspend fun getMufas(): Response<List<Mufa>>

    @GET("onu/unconfigured_onus")
    suspend fun getUnconfirmedOnus(): Response<List<Onu>>

    @GET("subscription/apply-coupon/{code}")
    suspend fun applyCoupon(@Path("code") code: String): Response<Coupon?>

    @PUT("plan")
    suspend fun updatePlan(@Body plan: Plan): Response<PlanResponse>

    @PUT("subscription/payment-commitment")
    suspend fun setPaymentCommitment(@Query("subscriptionId") id: Int): Response<Unit>

    @PUT("subscription/reactivate-service")
    suspend fun reactivateService(@Query("subscriptionId") subscription: Int): BaseResponse<Unit>

    @GET("subscription/find/nameAndLastName")
    suspend fun findSubscriptionByNameAndLastName(
        @Query("name") name: String?,
        @Query("lastName") lastName: String?
    ): Response<List<SubscriptionResponse>>

    @GET("subscription/debtors-with-cut-report-document")
    suspend fun downloadDebtorsCutOffCandidatesReportDocument(): Response<DownloadDocumentResponse>

    @PUT("subscription/cancel-subscription")
    suspend fun cancelSubscription(@Query("subscriptionId") subscriptionId: Int): Response<Unit>

    @PUT("subscription/update-subscription-data")
    suspend fun updateSubscriptionData(@Body subscriptionData: UpdateSubscriptionDataBody): Response<Unit>

    @GET("subscription/debtors-with-active-subscription-report-document")
    suspend fun downloadDebtorsWithActiveSubscriptionReportDocument(): Response<DownloadDocumentResponse>

    @GET("subscription/debtors-with-cancelled-subscription-report-document")
    suspend fun downloadDebtorsWithCancelledSubscriptionReportDocument(): Response<DownloadDocumentResponse>

    @GET("report/canceled-current-month")
    suspend fun downloadCancelledSubscriptionsFromCurrentMonth(): Response<DownloadDocumentResponse>

    @GET("report/canceled-past-month")
    suspend fun downloadCancelledSubscriptionsFromLastMonth(): Response<DownloadDocumentResponse>

    @GET("assistanceTicket/find")
    suspend fun getTicket(@Query("ticketId") ticketId: String): Response<AssistanceTicketResponse>
    @GET("assistanceTicket/findAll")
    suspend fun getTicketsByStatus(@Query("status") status: AssistanceTicketStatus): Response<List<AssistanceTicketResponse>>

    @PUT("assistanceTicket/updateStatus")
    suspend fun updateTicketState(
        @Query("ticketId") ticketId: Int,
        @Query("status") newStatus: AssistanceTicketStatus,
        @Query("userId") userId: Int
    ):Response<AssistanceTicketResponse>

    @POST("assistanceTicket")
    suspend fun createTicket(@Body value: AssistanceTicketRequest): Response<AssistanceTicketResponse>

    @GET("subscription/fastSearch")
    suspend fun findSubscriptionByNames(@Query("keyword") keyword: String): Response<List<SubscriptionFastSearchResponse>>

}

