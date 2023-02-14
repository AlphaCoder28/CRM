package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class LoadDescription(
    @SerializedName("LoadDescriptionID")
    val loadDescriptionID: Int,
    @SerializedName("LoadDescriptionName")
    val loadDescriptionName: String,
    @SerializedName("LoadDescriptionRemark")
    var loadDescriptionRemark: String
) {
    override fun toString(): String {
        return loadDescriptionName
    }
}