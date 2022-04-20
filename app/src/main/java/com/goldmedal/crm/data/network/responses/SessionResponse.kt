package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.SessionData
import com.google.gson.annotations.SerializedName

data class SessionResponse(
        @SerializedName("Data")
        val sessionData: List<SessionData?>?,


        val StatusCodeMessage: String?,

        val StatusCode: String?,


        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)