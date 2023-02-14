package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class Supply(
    @SerializedName("SupplyID")
    val supplyID: Int,
    @SerializedName("SupplyName")
    val supplyName: String,
    @SerializedName("SupplyRemark")
    val supplyRemark: String
)