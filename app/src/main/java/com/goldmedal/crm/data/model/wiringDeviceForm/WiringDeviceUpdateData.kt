package com.goldmedal.crm.data.model.wiringDeviceForm


import com.goldmedal.crm.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class WiringDeviceUpdateData(
    @SerializedName("Data")
    val wiringDeviceData: List<WiringDeviceUpdateDataItem>,
    @SerializedName("Errors")
    val errors: List<ErrorData>,
    @SerializedName("Size")
    val size: Int,
    @SerializedName("StatusCode")
    val statusCode: Int,
    @SerializedName("StatusCodeMessage")
    val statusCodeMessage: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("Version")
    val version: String
)