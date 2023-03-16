package com.example.cleanarchitecture.domain.domain.entity

data class PlanResponse(
    val id: String? =null,
    val name:String?=null,
    val price: Double?=null,
    val downloadSpeed: String?=null,
    val uploadSpeed: String?=null,
    ):java.io.Serializable
{
    override fun toString(): String {
        return name!!
    }
    override fun equals(other: Any?): Boolean {
        return id == (other as PlanResponse).id
    }
}
