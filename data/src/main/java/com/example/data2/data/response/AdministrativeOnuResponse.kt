package com.example.data2.data.response

class AdministrativeOnuResponse(
    val sn: String,
    val name: String,
    val mgmt_ip_service_port: String,
    val authorization_date: String,
    val custom_template_name: String,
    val onu_type_name: String,
    val onu: String,
    val port: String,
    val board: String,
    val olt_id: String,
    val olt_name: String,
    val unique_external_id: String
)