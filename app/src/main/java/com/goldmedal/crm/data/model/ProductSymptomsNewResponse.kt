package com.goldmedal.crm.data.model

import com.google.gson.annotations.SerializedName

data class ProductSymptomsNewResponse(
    @SerializedName("Data")
    val productSymptomsList: List<ProductSymptomsItem>,
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

data class ProductSymptomsItem(
    @SerializedName("Symptoms")
    val symptoms: String,
    @SerializedName("SymptomID")
    val symptomID: Int
)
