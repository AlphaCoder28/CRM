package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class Voltage(
    @SerializedName("VoltageID")
    val voltageID: Int,
    @SerializedName("VoltageName")
    val voltageName: String,
    @SerializedName("VoltageRemark")
    var voltageRemark: String
) {
    override fun toString(): String {
        return voltageName
    }
}