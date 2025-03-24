package com.example.data2.data.datasource

import com.example.cleanarchitecture.domain.domain.entity.AppVersion
import com.example.cleanarchitecture.domain.domain.entity.Coupon
import com.example.cleanarchitecture.domain.domain.entity.CustomerData
import com.example.cleanarchitecture.domain.domain.entity.DashBoardDataResponse
import com.example.cleanarchitecture.domain.domain.entity.DownloadDocumentResponse
import com.example.cleanarchitecture.domain.domain.entity.FixedCost
import com.example.cleanarchitecture.domain.domain.entity.Ip
import com.example.cleanarchitecture.domain.domain.entity.IpPool
import com.example.cleanarchitecture.domain.domain.entity.Loging
import com.example.cleanarchitecture.domain.domain.entity.Mufa
import com.example.cleanarchitecture.domain.domain.entity.NapBox
import com.example.cleanarchitecture.domain.domain.entity.NapBoxResponse
import com.example.cleanarchitecture.domain.domain.entity.NetworkDevice
import com.example.cleanarchitecture.domain.domain.entity.NetworkDeviceResponse
import com.example.cleanarchitecture.domain.domain.entity.Onu
import com.example.cleanarchitecture.domain.domain.entity.Outlay
import com.example.cleanarchitecture.domain.domain.entity.Payment
import com.example.cleanarchitecture.domain.domain.entity.Place
import com.example.cleanarchitecture.domain.domain.entity.PlaceResponse
import com.example.cleanarchitecture.domain.domain.entity.Plan
import com.example.cleanarchitecture.domain.domain.entity.PlanResponse
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrder
import com.example.cleanarchitecture.domain.domain.entity.ServiceOrderResponse
import com.example.cleanarchitecture.domain.domain.entity.Subscription
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionFastSearchResponse
import com.example.cleanarchitecture.domain.domain.entity.SubscriptionResponse
import com.example.cleanarchitecture.domain.domain.entity.Technician
import com.example.cleanarchitecture.domain.domain.entity.User
import com.example.cleanarchitecture.domain.domain.entity.extensions.PayerFinderResult
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.apirequestmodel.FixedCostRequest
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.MigrationRequest
import com.example.data2.data.apirequestmodel.MoveOnuRequest
import com.example.data2.data.apirequestmodel.UpdateSubscriptionDataBody
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.response.AdministrativeOnuResponse
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus
import com.example.data2.data.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

    @GET("place/findByLocation")
    suspend fun findPlaceByLatLng(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<PlaceResponse>

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
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
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
    suspend fun reactivateService(
        @Query("subscriptionId") subscription: Int,
        @Query("responsibleId") responsibleId: Int
    ): BaseResponse<Unit>

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

    @PUT("assistanceTicket/assignTicketToUser")
    suspend fun assignSupportTicket(
        @Query("ticketId") ticketId: Int,
        @Query("userId") userId: Int,
    ): Response<AssistanceTicketResponse>


    @PUT("assistanceTicket/closeUnattendedTicket")
    suspend fun closeUnattendedSupportTicket(
        @Query("ticketId") ticketId: Int,
        @Query("userId") userId: Int,
    ): Response<AssistanceTicketResponse>

    @Multipart
    @PUT("assistanceTicket/closeAttendedTicket")
    suspend fun closeAttendedTicket(
        @Query("ticketId") ticketId: Int,
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<AssistanceTicketResponse>


    @POST("assistanceTicket")
    suspend fun createTicket(@Body value: AssistanceTicketRequest): Response<AssistanceTicketResponse>

    @GET("subscription/fastSearch")
    suspend fun findSubscriptionByNames(@Query("keyword") keyword: String): Response<List<SubscriptionFastSearchResponse>>

    @PUT("subscription/migration")
    suspend fun doMigration(@Body migrationRequest: MigrationRequest): BaseResponse<SubscriptionResponse>

    @GET("onu/getBySn")
    suspend fun getOnuBySn(@Query("onuSn") onuSn: String): BaseResponse<AdministrativeOnuResponse>

    @DELETE("onu")
    suspend fun deleteOnuFromOlt(@Query("onuExternalId") onuExternalId: String): BaseResponse<String>

    @POST("outlay")
    suspend fun saveOutlay(@Body outlay: Outlay): BaseResponse<Unit>

    @GET("payment/getElectronicPayers")
    suspend fun getElectronicPayers(@Query("subscriptionId") subscriptionId: Int): BaseResponse<List<String>>

    @PUT("subscription/update-customer-data")
    suspend fun updateCustomerData(@Body customer: CustomerData): Response<Unit>

    @GET("subscription/{subscriptionId}")
    suspend fun subscriptionById(@Path("subscriptionId") subscriptionId: Int): Response<SubscriptionResponse>

    @PUT("subscription/changeNapBox")
    suspend fun changeSubscriptionNapBox(
        @Body request: MoveOnuRequest
    ): Response<Unit>

    @GET("app/check_version")
    suspend fun getRemoteAppVersion(): Response<AppVersion>

    @GET("assistanceTicket/byDateRange")
    suspend fun getTicketsByDateAndStatusRange(
        @Query("status") status: AssistanceTicketStatus,
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long
    ): Response<List<AssistanceTicketResponse>>

    @POST("fixed_cost/")
    suspend fun saveFixedCost(@Body fixedCostRequest: FixedCostRequest): Response<Unit>

    @GET("fixed_cost/")
    suspend fun getAllFixedCosts(): Response<List<FixedCost>>

    @GET("subscription/findByElectronicPayerName")
    suspend fun findPaymentByElectronicPayerName(@Query("electronicPayerName") electronicPayerName: String): BaseResponse<List<PayerFinderResult>>

}

