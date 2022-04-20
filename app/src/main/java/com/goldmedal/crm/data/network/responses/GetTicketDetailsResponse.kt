package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.google.gson.annotations.SerializedName

data class GetTicketDetailsResponse(
        @SerializedName("Data")
        val ticketDetails: List<GetTicketDetailsData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)