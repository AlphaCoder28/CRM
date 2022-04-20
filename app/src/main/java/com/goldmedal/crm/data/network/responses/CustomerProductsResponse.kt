package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.CustomerProductsData
import com.google.gson.annotations.SerializedName

data class CustomerProductsResponse(
    @SerializedName("Data")
    val data: List<CustomerProductsData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)