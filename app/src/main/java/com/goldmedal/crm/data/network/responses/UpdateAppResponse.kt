package com.goldmedal.crm.data.network.responses

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class UpdateAppResponse : ArrayList<AppupdateItem>()

data class AppupdateItem(
    @SerializedName("data")
    val appUpdateData: List<AppUpdateData>,
    val message: String,
    val result: Boolean,
    val servertime: String
)

data class AppUpdateData(
    val appUrl: String
)
