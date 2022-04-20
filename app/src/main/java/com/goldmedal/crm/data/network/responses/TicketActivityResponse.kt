package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.TicketActivityData
import com.google.gson.annotations.SerializedName

data class TicketActivityResponse(
    @SerializedName("Data")
    val tktActivity: List<TicketActivityData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)