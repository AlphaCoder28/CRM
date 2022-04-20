package com.goldmedal.crm.data.network.responses

import com.goldmedal.crm.data.model.ErrorData
import com.goldmedal.crm.data.model.PincodeWiseStateDistrictData
import com.google.gson.annotations.SerializedName

data class PincodeWiseStateDistrictResponse(
        @SerializedName("Data")
        val geographicalData: List<PincodeWiseStateDistrictData>?,

        val StatusCode: String?,


        val StatusCodeMessage: String?,


        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)