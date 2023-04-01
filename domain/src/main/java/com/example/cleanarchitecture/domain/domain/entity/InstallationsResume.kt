package com.example.cleanarchitecture.domain.domain.entity

data class InstallationResume(
    val FIBER: String,
    val WIRELESS: String
){
    fun getFiberResume(): String {
        return "Fibra $FIBER"
    }

    fun getWirelessResume(): String {
        return "Inalámbrico $WIRELESS"
    }
}