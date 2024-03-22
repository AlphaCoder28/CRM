package com.goldmedal.crm.data.model

import com.goldmedal.crm.data.network.responses.ReplacementReasonItem
import com.google.gson.annotations.SerializedName

data class StockListResponse(
    @SerializedName("Data")
    val stockItemsList: List<StockItemData>,
    @SerializedName("Errors")
    val errors: List<ErrorData>,
    @SerializedName("Size")
    val size: Int,
    @SerializedName("StatusCode")
    val statusCode: Int,
    @SerializedName("StatusCodeMessage")
    val statusCodeMessage: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("Version")
    val version: String
)

data class StockItemData(
    @SerializedName("EngID")
    val engID: Int,
    @SerializedName("ItemID")
    val itemID: Int,
    @SerializedName("ItemName")
    val itemName: String,
    @SerializedName("Qty")
    val qty: Int
)