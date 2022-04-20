package com.goldmedal.crm.data.network.responses


import com.goldmedal.crm.data.db.entities.User
import com.goldmedal.crm.data.model.ErrorData
import com.google.gson.annotations.SerializedName

//<!--added by shetty 6 jan 21-->
data class LoginResponse(
        @SerializedName("Data")
        val user: List<User>?,


        val StatusCodeMessage: String?,

        val StatusCode: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)