package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.*
import com.google.gson.annotations.SerializedName

data class UpdateProfilePhotoResponse(
        @SerializedName("Data")
        val updatePhoto: List<UpdateProfilePhotoData>?,
        val StatusCodeMessage: String?,
        val StatusCode: Int?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)

