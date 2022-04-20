package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.*
import com.google.gson.annotations.SerializedName

data class SelectPartListByUserResponse(
        @SerializedName("Data")
        val getSelectPartsByUserList: List<SelectPartsListByUserData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)