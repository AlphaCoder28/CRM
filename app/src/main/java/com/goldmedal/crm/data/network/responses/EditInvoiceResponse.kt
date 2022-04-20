package com.goldmedal.crm.data.network.responses
import com.goldmedal.crm.data.model.EditInvoiceData
import com.goldmedal.crm.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class EditInvoiceResponse(
    @SerializedName("Data")
    val data: List<EditInvoiceData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)
