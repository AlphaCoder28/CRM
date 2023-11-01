package com.goldmedal.crm.data.model

import com.google.gson.annotations.SerializedName

data class RepairTypeResponse(
    @SerializedName("Data")
    val repairTypeList: List<RepairTypeItem>,
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

data class RepairTypeItem(
    @SerializedName("RepairType")
    val repairType: String,
    @SerializedName("RepairTypeID")
    val repairTypeID: Int
)
