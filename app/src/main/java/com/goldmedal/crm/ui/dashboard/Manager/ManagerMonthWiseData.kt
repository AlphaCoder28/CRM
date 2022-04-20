package com.goldmedal.crm.ui.dashboard.Manager

import com.goldmedal.crm.data.model.ErrorData

data class ManagerMonthWiseData(
    val Data: List<MonthwiseData>,
    val Errors: List<ErrorData?>?,
    val Size: Int,
    val StatusCode: Int,
    val StatusCodeMessage: String,
    val Timestamp: String,
    val Version: String


)

 class MonthwiseData(
    val Closetk: Int,
    val MonthName: String,
    val MonthNo: Int,
    val Opentk: Int
)