package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class WiringDeviceData(
    @SerializedName("BrandName")
    val brandName: Any,
    @SerializedName("FaultyChannelDetails")
    val faultyChannelDetails: FaultyChannelDetails,
    @SerializedName("FaultyChannelList")
    val faultyChannelList: List<FaultyChannel>,
    @SerializedName("LoadDescriptionList")
    val loadDescriptionList: List<LoadDescription>,
    @SerializedName("PhaseList")
    val phaseList: List<Phase>,
    @SerializedName("PowerFactor")
    val powerFactor: Any,
    @SerializedName("ShortRemark")
    val shortRemark: Any,
    @SerializedName("SupplyList")
    val supplyList: List<Supply>,
    @SerializedName("TypeofLEDList")
    val typeofLEDList: List<TypeLED>,
    @SerializedName("VoltageList")
    val voltageList: List<Voltage>
)