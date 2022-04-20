package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetDashboardData
import com.google.gson.annotations.SerializedName

data class DashboardResponse(
        @SerializedName("Data")
        val data: List<GetDashboardData>?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        val StatusCode: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)