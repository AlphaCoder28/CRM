package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetTimeSlots
import com.goldmedal.crm.data.model.SymptomsData
import com.goldmedal.crm.data.model.closedOtpData
import com.google.gson.annotations.SerializedName

data class ClosedOtpResponse(
        @SerializedName("Data")
        val closedOtp: List<closedOtpData>?,
        val StatusCodeMessage: String?,
        val StatusCode: Int?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)

