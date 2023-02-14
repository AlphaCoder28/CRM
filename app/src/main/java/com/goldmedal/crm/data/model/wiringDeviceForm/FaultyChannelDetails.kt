package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class FaultyChannelDetails(
    @SerializedName("L1_Current")
    val l1Current: String,
    @SerializedName("L1_PF")
    val l1PF: String,
    @SerializedName("L1_Wattage")
    val l1Wattage: String,
    @SerializedName("L2_Current")
    val l2Current: String,
    @SerializedName("L2_PF")
    val l2PF: String,
    @SerializedName("L2_Wattage")
    val l2Wattage: String,
    @SerializedName("L3_Current")
    val l3Current: String,
    @SerializedName("L3_PF")
    val l3PF: String,
    @SerializedName("L3_Wattage")
    val l3Wattage: String,
    @SerializedName("L4_Current")
    val l4Current: String,
    @SerializedName("L4_PF")
    val l4PF: String,
    @SerializedName("L4_Wattage")
    val l4Wattage: String
)