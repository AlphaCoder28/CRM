package com.goldmedal.crm.data.model

data class EditInvoiceData(
    val CustName: String,
    val CustomerID: Int,
    val FinalTotal: String,
    val IsPaid: Int,
    val PaymentMethod: String,
    val PaymentStatus: String,
    val SlNo: Int,
    val TicketID: Int,
    val TktNo: String
)
