package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.goldmedal.crm.data.model.GetPartsRequirementData
import com.google.gson.annotations.SerializedName

data class GetPartsRequirementResponse(
        @SerializedName("Data")
        val getPartsRequirementData: List<GetPartsRequirementData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)