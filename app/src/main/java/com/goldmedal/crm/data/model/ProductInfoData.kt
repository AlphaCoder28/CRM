package com.goldmedal.crm.data.model

data class ProductInfoData(
    val ManufactureDate: String,
    val ProductID: Int,
    val ProductName: String,
    val categoryid: Int,
    val categorynm: String,
    val divisionid: Int,
    val divisionnm: String,
    val QRCode: String,
    val Warranty: Int,
    val PurchaseDt: String,
    val Out: Int,
    val ProductDescription: String
)