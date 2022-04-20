package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData
import com.google.gson.annotations.SerializedName

data class GetAssignedTicketResponse(
    @SerializedName("Data")
        val allAssignedTkts: List<GetAllAssignedTicketsData>?,

    @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

    val StatusCode: String?,

    @SerializedName("Timestamp")
        val servertime: String?,

    val Errors: List<ErrorData?>?
)