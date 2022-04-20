package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.google.gson.annotations.SerializedName

data class GetTicketsCountResponse(
        @SerializedName("Data")
        val ticketsCount: List<GetTicketsCountData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)