package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetInvoiceListData
import com.google.gson.annotations.SerializedName

data class GetInvoiceListResponse(
        @SerializedName("Data")
        val getInvoiceList: List<GetInvoiceListData>?,
        val StatusCodeMessage: String?,
        val StatusCode: Int?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)