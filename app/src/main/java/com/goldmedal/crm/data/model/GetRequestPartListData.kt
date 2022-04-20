package com.goldmedal.crm.data.model

data class GetRequestPartListData(
    val ContactNo: String,
    val CustName: String,
    val TktNo: String,
    val TktStatus: String,
    val RequestDate: String,
    val RequestNo: String,
    val TotalPart: Int,
    val TotalQuantity: Int,
    val slno: Int
)
