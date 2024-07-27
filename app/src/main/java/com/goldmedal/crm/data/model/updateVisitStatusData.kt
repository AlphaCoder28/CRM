package com.goldmedal.crm.data.model

import com.google.gson.annotations.SerializedName

data class UpdateVisitStatusData(
//    val Out: Int? = null
    val ActionStatus: Int? = null,
    val ActionIDStatus: Int? = null,
    @SerializedName("StatusCode")
    val statusCode: String,
    @SerializedName("StatusMessage")
    val statusMessage: String,
)