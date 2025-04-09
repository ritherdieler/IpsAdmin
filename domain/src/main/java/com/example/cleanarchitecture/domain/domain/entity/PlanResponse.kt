package com.example.cleanarchitecture.domain.domain.entity

data class PlanResponse(
    var id: String? = null,
    var name: String? = null,
    var price: Double? = null,
    var downloadSpeed: String? = null,
    var uploadSpeed: String? = null,
    var type: PlanType
) : java.io.Serializable {
    override fun toString(): String {
        return "${name?.capitalize()}, Precio: ${priceToString()}"
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as PlanResponse).id
    }

    fun getPlanType() = when (type) {
        PlanType.WIRELESS -> "W"
        PlanType.FIBER -> "F"
    }

    fun priceToString(): String {
        return "S/$price"
    }

    fun downloadSpeedToString(): String {
        return "${downloadSpeed}Mbps"
    }

    fun uploadSpeedToString(): String {
        return "${uploadSpeed}Mbps"
    }

    enum class PlanType {
        WIRELESS, FIBER
    }
}
