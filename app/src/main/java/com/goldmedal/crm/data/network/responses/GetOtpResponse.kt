package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetOtpData
import com.google.gson.annotations.SerializedName

data class GetOtpResponse(
        @SerializedName("Data")
        val otpData: List<GetOtpData?>?,


        val StatusCodeMessage: String?,

        val StatusCode: String?,


        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)