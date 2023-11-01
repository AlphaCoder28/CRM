package com.goldmedal.crm.data.model

import com.google.gson.annotations.SerializedName

data class DefectReasonsResponse(
    @SerializedName("Data")
    val defectReasonsList: List<DefectReasonItem>,
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

data class DefectReasonItem(
    @SerializedName("DefectReason")
    val defectReason: String,
    @SerializedName("DefectReasonID")
    val defectReasonID: Int
)
