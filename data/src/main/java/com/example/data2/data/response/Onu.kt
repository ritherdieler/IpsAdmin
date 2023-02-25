package com.example.data2.data.response

data class Onu(
    val board: String,
    val olt_id: String,
    val onu: String,
    val onu_type_id: String,
    val onu_type_name: String,
    val pon_type: String,
    val port: String,
    val sn: String
)