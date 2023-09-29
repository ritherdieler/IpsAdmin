package com.example.data2.data.repository

import android.content.SharedPreferences
import com.example.cleanarchitecture.domain.domain.entity.*
import com.example.cleanarchitecture.domain.domain.entity.FireBaseResponse
import com.example.data2.data.api.RestApiServices
import com.example.data2.data.api.SendMessagingCloudApi
import com.example.data2.data.apirequestmodel.AssistanceTicketRequest
import com.example.data2.data.apirequestmodel.IpPoolRequest
import com.example.data2.data.apirequestmodel.MigrationRequest
import com.example.data2.data.apirequestmodel.SearchPaymentsRequest
import com.example.data2.data.apirequestmodel.UpdateSubscriptionDataBody
import com.example.data2.data.apirequestmodel.UpdateSubscriptionPlanBody
import com.example.data2.data.response.AssistanceTicketResponse
import com.example.data2.data.response.AssistanceTicketStatus
import com.example.data2.data.utils.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

/**
 * Created by Sergio Carrillo Diestra on 19/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
class Repository : IRepository, KoinComponent {

    private val restApiServices: RestApiServices by inject()
    private val sendMessagingCloudApi: SendMessagingCloudApi by inject()
    private val prefs: SharedPreferences by inject()


    override suspend fun registerUser(user: User): User {
        val response = restApiServices.registerUser(user)
        return when (response.code()) {
            HttpCodes.OK -> response.body()!!
            HttpCodes.CONFLICT -> throw Exception("El usuario ya existe, por favor use otro")
            else -> throw Exception("Ocurrió un error inesperado, contacte con soporte técnico")
        }
    }

    override suspend fun doLogin(login: Loging): User {
        val response = restApiServices.doLoging(login)

        return when (response.code()) {
            HttpCodes.OK -> {
                val userSession = response.body()!!
                userSession.apply { password = login.password }
                saveUserSession(userSession, login.checkBox)
                response.body()!!
            }
            HttpCodes.NOT_FOUND -> throw Exception("Usuario o Contraseña Incorrecta")
            else -> throw Exception("Ocurrió un error inesperado, contacte con soporte técnico")
        }
    }

    override suspend fun saveUserSession(user: User, rememberSessionCheckBoxStatus: Boolean?) {
        val editor = prefs.edit()
        rememberSessionCheckBoxStatus?.let {
            editor.putBoolean(
                REMEMBER_CHECKBOX_STATUS,
                rememberSessionCheckBoxStatus
            )
        }
        editor.putString(SESSION_NAME, user.name)
        editor.putString(SESSION_LAST_NAME, user.lastName)
        editor.putString(SESSION_USER_NAME, user.username)
        editor.putString(SESSION_PASSWORD, user.password)
        editor.putString(SESSION_DNI, user.dni)
        editor.putString(SESSION_EMAIL, user.email)
        editor.putString(SESSION_PHONE, user.phone)

        editor.putString(SESSION_TYPE, user.type.toString())
        user.id?.let { editor.putInt(SESSION_ID, it) }
        editor.putBoolean(SESSION_VERIFIED, user.verified)
        editor.apply()
    }

    override fun getUserSession(): User? {
        if (!prefs.contains(SESSION_ID)) return null
        val userType = prefs.getString(SESSION_TYPE, "")
        return User(
            id = prefs.getInt(SESSION_ID, 0),
            name = prefs.getString(SESSION_NAME, "")!!,
            lastName = prefs.getString(SESSION_LAST_NAME, "")!!,
            type = User.UserType.valueOf(userType!!),
            username = prefs.getString(SESSION_USER_NAME, "")!!,
            password = prefs.getString(SESSION_PASSWORD, "")!!,
            verified = prefs.getBoolean(SESSION_VERIFIED, false),
            dni = prefs.getString(SESSION_DNI, "")!!,
            email = prefs.getString(SESSION_EMAIL, "")!!,
            phone = prefs.getString(SESSION_PHONE, "")!!,
        )
    }

    override fun getRememberSessionCheckBoxStatus(): Boolean {
        return prefs.getBoolean(REMEMBER_CHECKBOX_STATUS, false)
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

    override suspend fun getDevices(): List<NetworkDeviceResponse> {
        TODO("Not yet implemented")
    }


    override suspend fun registerSubscription(subscription: Subscription): Subscription {
        val response = restApiServices.registerSubscription(subscription)

        return when (response.status) {
            HttpCodes.OK -> response.data!!
            else -> throw Exception("Ocurrion un error inesperado, contacte con soporte técnico")
        }
    }

    override suspend fun getPlans(): List<PlanResponse> {
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

    override suspend fun getPlaces(): List<PlaceResponse> {
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

    override suspend fun getNapBoxes(): List<NapBoxResponse> {
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

    override suspend fun findSubscriptionByDNI(id: String): List<SubscriptionResponse> {
        val response = restApiServices.findSubscriptionByDNI(id)
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

    override suspend fun updateSubscriptionPlan(subscription: UpdateSubscriptionPlanBody): SubscriptionResponse {
        val response = restApiServices.updateSubscriptionPlan(subscription)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }



    override suspend fun downloadDebtorWithActiveSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadDebtorsWithActiveSubscriptionReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadPaymentCommitmentSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadWithPaymentCommitmentSubscriptionsReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadSuspendedSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadSuspendedSubscriptionsReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadCutOffSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadCutOffSubscriptionsReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadPastMonthDebtorsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadPastMontDebtorsSubscriptionsReportDocument()
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

    override suspend fun startServiceCut() {
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

    override suspend fun editNapBox(napBox: NapBox): NapBoxResponse {
        val response = restApiServices.editNapBox(napBox)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun editServiceOrder(serviceOrder: ServiceOrder): ServiceOrderResponse {
        val response = restApiServices.editServiceOrder(serviceOrder)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getMufas(): List<Mufa> {
        val response = restApiServices.getMufas()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("No se pudieron recuperar las mufas")
        }
    }

    override suspend fun getUnconfirmedOnus(): List<Onu> {
        val response = restApiServices.getUnconfirmedOnus()
        return if (response.code() == 200) response.body()!!
        else throw Exception("Error generico")
    }

    override suspend fun applyCoupon(code: String): Coupon? {

        val response = restApiServices.applyCoupon(code)

        return when (response.code()) {
            200 -> response.body()
            404 -> null
            else -> throw Exception("Ocurrio un error en la activacion del cupon")
        }

    }

    override suspend fun findSubscriptionBySubscriptionDate(
        startDate: Long,
        endDate: Long
    ): List<SubscriptionResponse> {
        val response = restApiServices.findSubscriptionBySubscriptionDate(startDate, endDate)
        return when (response.code()) {
            200 -> response.body()!!.ifEmpty { listOf() }
            else -> throw Exception("Error")
        }
    }

    override suspend fun sendCloudMessaging(body: FirebaseBody?): FireBaseResponse {
        val response = sendMessagingCloudApi.sendCloudMessaging(body)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error en la notificacion")
        }
    }

    override suspend fun updatePlan(plan: Plan): PlanResponse {
        val response = restApiServices.updatePlan(plan)
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Ocurrio un error al registrar el plan")
        }
    }

    override suspend fun savePaymentCommitment(id: Int) {
        val response = restApiServices.setPaymentCommitment(id)
        when (response.code()) {
            HttpCodes.OK -> {}
            else -> throw Exception("Ocurrio un error al registrar el compromiso de pago, contacte con el administrador")
        }
    }

    override suspend fun reactivateService(subscription: SubscriptionResponse) {
        val response = restApiServices.reactivateService(subscription.id)
        when(response.status){
            HttpCodes.OK ->{}
            else -> throw Exception(response.error)
        }
    }

    override suspend fun findSubscriptionByNameAndLastName(
        name: String?,
        lastName: String?
    ): List<SubscriptionResponse> {
        val response = restApiServices.findSubscriptionByNameAndLastName(name, lastName)
        return when (response.code()) {
            200 -> response.body()!!.ifEmpty { listOf() }
            else -> throw Exception("Error")
        }
    }

    override suspend fun downloadDebtorsCutOffCandidatesSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadDebtorsCutOffCandidatesReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun cancelSubscription(subscription: SubscriptionResponse) {
        val response = restApiServices.cancelSubscription(subscription.id)
        when(response.code()){
            HttpCodes.OK ->{}
            else -> throw Exception("No se pudo cancelar el servicio, vuelva a intentarlos mas tarde")
        }
    }

    override suspend fun updateSubscriptionData(subscriptionData: UpdateSubscriptionDataBody) {
        val response = restApiServices.updateSubscriptionData(subscriptionData)
        when(response.code()){
            HttpCodes.OK ->{}
            else -> throw Exception("No se pudo actualizar los datos de la suscripcion")
        }
    }

    override suspend fun downloadDebtorWithCancelledSubscriptionsReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadDebtorsWithCancelledSubscriptionReportDocument()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadCancelledSubscriptionsFromCurrentMonthReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadCancelledSubscriptionsFromCurrentMonth()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun downloadCancelledSubscriptionsFromPastMonthReport(): DownloadDocumentResponse {
        val response = restApiServices.downloadCancelledSubscriptionsFromLastMonth()
        if (response.code() == 200) {
            return response.body()!!
        } else {
            throw Exception("Error")
        }
    }

    override suspend fun getTicket(ticketId: String): AssistanceTicketResponse {
        return restApiServices.getTicket(ticketId).successOrThrow()
    }

    override suspend fun getTicketsByStatus(pending: AssistanceTicketStatus): List<AssistanceTicketResponse> {
        return restApiServices.getTicketsByStatus(pending).successOrThrow()
    }

    override suspend fun updateTicketState(
        id: Int,
        newStatus: AssistanceTicketStatus,
        userId: Int
    ): AssistanceTicketResponse {
        return restApiServices.updateTicketState(id, newStatus, userId).successOrThrow()
    }

    override suspend fun createTicket(value: AssistanceTicketRequest): AssistanceTicketResponse {
        return restApiServices.createTicket(value).successOrThrow()
    }

    override suspend fun findSubscriptionByNames(names: String): List<SubscriptionFastSearchResponse> {
        return restApiServices.findSubscriptionByNames(names).successOrThrow()
    }

    override suspend fun doMigration(migrationRequest: MigrationRequest): SubscriptionResponse {
        val response = restApiServices.doMigration(migrationRequest)

        return when (response.status) {
            HttpCodes.OK -> response.data!!
            else -> throw Exception(response.error)
        }
    }
}

private fun <T> Response<T>.successOrThrow(): T {
    if (isSuccessful) {
        return body()!!
    } else {
        throw Exception("Error")
    }
}
