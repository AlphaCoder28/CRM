package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class FaultyChannel(
    @SerializedName("FaultyChannelID")
    val faultyChannelID: Int,
    @SerializedName("FaultyChannelName")
    val faultyChannelName: String,
    @SerializedName("FaultyChannelRemark")
    var faultyChannelRemark: String
) {
    override fun toString(): String {
        return faultyChannelName
    }
}