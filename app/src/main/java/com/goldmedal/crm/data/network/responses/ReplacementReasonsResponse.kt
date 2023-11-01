package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class ReplacementReasonsResponse(
    @SerializedName("Data")
    val replacementReasonsList: List<ReplacementReasonItem>,
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

data class ReplacementReasonItem(
    @SerializedName("ReplacementReason")
    val replacementReason: String,
    @SerializedName("ReplacementReasonID")
    val replacementReasonID: Int
)