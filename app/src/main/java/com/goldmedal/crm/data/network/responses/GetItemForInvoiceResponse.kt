package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetItemForInvoiceData
import com.google.gson.annotations.SerializedName

data class GetItemForInvoiceResponse(
        @SerializedName("Data")
        val getItemForInvoice: List<GetItemForInvoiceData>?,
        val StatusCodeMessage: String?,
        val StatusCode: Int?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)