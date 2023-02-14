package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class TypeLED(
    @SerializedName("TypeofLEDID")
    val typeofLEDID: Int,
    @SerializedName("TypeofLEDName")
    val typeofLEDName: String,
    @SerializedName("TypeofLEDRemark")
    var typeofLEDRemark: String
) {
    override fun toString(): String {
        return typeofLEDName
    }
}