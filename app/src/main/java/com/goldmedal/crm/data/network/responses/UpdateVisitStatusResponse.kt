package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.UpdateVisitStatusData
import com.google.gson.annotations.SerializedName

data class UpdateVisitStatusResponse(
        @SerializedName("Data")
        val updateStatus: List<UpdateVisitStatusData>?,

        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)