package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetAppointmentsData
import com.google.gson.annotations.SerializedName

data class GetAppointmentsResponse(
    @SerializedName("Data")
        val todayAppointment: List<GetAppointmentsData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)