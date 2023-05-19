package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class WiringDeviceData(
    @SerializedName("BrandName")
    val brandName: String,
    @SerializedName("FaultyChannelDetails")
    val faultyChannelDetails: FaultyChannelDetails,
    @SerializedName("FaultyChannelList")
    val faultyChannelList: List<FaultyChannel>,
    @SerializedName("LoadDescriptionList")
    val loadDescriptionList: List<LoadDescription>,
    @SerializedName("PhaseList")
    val phaseList: List<Phase>,
    @SerializedName("PowerFactor")
    val powerFactor: String,
    @SerializedName("ShortRemark")
    val shortRemark: String,
    @SerializedName("SupplyList")
    val supplyList: List<Supply>,
    @SerializedName("TypeofLEDList")
    val typeofLEDList: List<TypeLED>,
    @SerializedName("VoltageList")
    val voltageList: List<Voltage>
)