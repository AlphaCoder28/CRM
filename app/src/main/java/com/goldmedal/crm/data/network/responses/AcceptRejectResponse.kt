package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.google.gson.annotations.SerializedName

data class AcceptRejectResponse(
        @SerializedName("Data")
        val acceptRejectTkt: List<AcceptRejectTicket>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)