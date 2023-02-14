package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class WiringDeviceUpdateDataItem(
    @SerializedName("StatusCode")
    val statusCode: Int,
    @SerializedName("StatusMessage")
    val statusMessage: String
)