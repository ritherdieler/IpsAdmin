package com.example.cleanarchitecture.domain.entity

data class InstallationResume(
    val FIBER: String,
    val WIRELESS: String
){
    fun getFiberResume(): String {
        return FIBER
    }

    fun getWirelessResume(): String {
        return WIRELESS
    }
}