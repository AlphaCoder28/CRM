package com.goldmedal.crm.data.model

data class TicketsByProductsData(
    val AcceptDate: String?,
    val AppointmentDate: String?,
    val CategoryID: Int?,
    val CategoryName: String?,
    val DivisionID: Int?,
    val DivisionName: String?,
    val EnginnerRemarks: String?,
    val ProductId: Int?,
    val ProductIssues: String?,
    val ProductName: String?,
    val ProductQRCode: String?,
    val PurchaseDate: String?,
    val ServiceDate: String?,
    val Symptoms: String?,
    val TicketDate: String?,
    val TicketId: Int?,
    val TicketNo: String?,
    val TicketStatus: String?,
    val TicketTotalCost: Int?,
    val WarrantyUptoDate: String?,
    val isFreeService: String?
)