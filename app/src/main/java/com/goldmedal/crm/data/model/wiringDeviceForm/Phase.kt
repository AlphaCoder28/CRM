package com.goldmedal.crm.data.model.wiringDeviceForm


import com.google.gson.annotations.SerializedName

data class Phase(
    @SerializedName("PhaseID")
    val phaseID: Int,
    @SerializedName("PhaseName")
    val phaseName: String,
    @SerializedName("PhaseRemark")
    val phaseRemark: String
)