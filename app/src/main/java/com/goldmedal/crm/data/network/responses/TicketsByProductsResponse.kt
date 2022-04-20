package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.TicketsByProductsData
import com.google.gson.annotations.SerializedName

data class TicketsByProductsResponse(
    @SerializedName("Data")
    val data: List<TicketsByProductsData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)