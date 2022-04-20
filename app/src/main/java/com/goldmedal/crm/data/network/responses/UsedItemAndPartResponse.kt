package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.*
import com.google.gson.annotations.SerializedName

data class UsedItemAndPartResponse(
        @SerializedName("Data")
        val itemAndPartData: List<UsedItemAndPartData>?,
        val StatusCodeMessage: String?,
        val StatusCode: Int?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)

