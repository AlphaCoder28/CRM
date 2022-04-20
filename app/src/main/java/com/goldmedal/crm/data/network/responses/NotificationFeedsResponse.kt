package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.NotificationFeedsData
import com.google.gson.annotations.SerializedName

data class NotificationFeedsResponse(
        @SerializedName("Data")
        val feeds: List<NotificationFeedsData>?,

        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)