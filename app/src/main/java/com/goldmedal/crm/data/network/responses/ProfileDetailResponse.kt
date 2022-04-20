package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.profileDetailData
import com.google.gson.annotations.SerializedName


data class ProfileDetailResponse(
    @SerializedName("Data")
    val profileDetailMain: List<profileDetailData>?,

    val StatusCode: String?,

    @SerializedName("StatusCodeMessage")
    val StatusCodeMessage: String?,

    @SerializedName("Timestamp")
    val servertime: String?,

    val Errors: List<ErrorData?>?
)