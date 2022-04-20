package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.db.entities.TicketHistoryData
import com.goldmedal.crm.data.model.ContactsData
import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.TicketActivityData
import com.google.gson.annotations.SerializedName

data class TicketHistoryResponse(
    @SerializedName("Data")
    val data: List<TicketHistoryData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)