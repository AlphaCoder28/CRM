package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ContactsData
import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetDashboardData
import com.google.gson.annotations.SerializedName

data class CustomerContactsResponse(
    @SerializedName("Data")
    val contactsData: List<ContactsData>?,
    val StatusCodeMessage: String?,
    val StatusCode: String?,
    val Timestamp: String?,
    val Errors: List<ErrorData?>?
)