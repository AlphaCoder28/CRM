package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.GetTimeSlots
import com.goldmedal.crm.data.model.SymptomsData
import com.google.gson.annotations.SerializedName

data class TimeSlotResponse(
        @SerializedName("Data")
        val timeSlots: List<GetTimeSlots>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)