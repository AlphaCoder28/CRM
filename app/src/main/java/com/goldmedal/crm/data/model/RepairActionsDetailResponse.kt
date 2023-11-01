package com.goldmedal.crm.data.model

import com.google.gson.annotations.SerializedName

data class RepairActionsDetailResponse(
    @SerializedName("Data")
    val repairActionDetailList: List<RepairActionDetailItem>,
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

data class RepairActionDetailItem(
    @SerializedName("RepairAction")
    val repairAction: String,
    @SerializedName("RepairActionID")
    val repairActionID: Int
)
