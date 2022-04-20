package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetTicketDetailsForEngineerData
import com.google.gson.annotations.SerializedName

data class GetTicketDetailsForEngineerResponse(
        @SerializedName("Data")
        val ticketDetails: List<GetTicketDetailsForEngineerData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)