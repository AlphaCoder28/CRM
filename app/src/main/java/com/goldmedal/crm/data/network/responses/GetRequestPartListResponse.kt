package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.goldmedal.crm.data.model.GetPartsData
import com.goldmedal.crm.data.model.GetRequestPartListData
import com.google.gson.annotations.SerializedName

data class GetRequestPartListResponse(
        @SerializedName("Data")
        val getRequestPartListData: List<GetRequestPartListData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)