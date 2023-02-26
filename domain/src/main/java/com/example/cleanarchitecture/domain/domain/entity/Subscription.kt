package com.example.cleanarchitecture.domain.domain.entity

/**
 * Created by Sergio Carrillo Diestra on 30/11/2022.
 * scarrillo.peruapps@gmail.com
 * Peru Apps
 * Huacho, Peru.
 *
 **/
 class Subscription(
    var id: Int? = null,
    var firstName: String,
    var lastName: String,
    var dni: String,
    var password:String,
    var address:String,
    var phone:String,
    var subscriptionDate:Long? = null,
    var isNew:Boolean? = null,
    var serviceIsSuspended: Boolean? = null,
    var planId:String?=null,
    var additionalDevices: List<Int>?=null,
    var placeId:String?=null,
    var location: GeoLocation? = null,
    var technicianId: Int? = null,
    var napBoxId:String?=null,
    var hostDeviceId:Int?=null,

    ):java.io.Serializable
{
  override fun toString(): String {
    return firstName
  }

  override fun equals(other: Any?): Boolean {
    return if (other is Subscription) {
      other.id == id
    } else {
      false
    }
  }

}
