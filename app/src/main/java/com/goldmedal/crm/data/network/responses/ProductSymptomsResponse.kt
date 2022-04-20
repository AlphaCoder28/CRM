package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.ProductInfoData
import com.goldmedal.crm.data.model.SymptomsData
import com.google.gson.annotations.SerializedName

data class ProductSymptomsResponse(
    @SerializedName("Data")
    val symptomsList: List<SymptomsData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)