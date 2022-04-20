package com.goldmedal.crm.data.model

data class GetAppointmentsData(
    val CustomerName: String,
    val CustAddress: String,
    val ProductName: String,
    val ProductIssues: String,
    val AppointmentDate: String,
    val TicketID: Int,
    val TimeSlot: Any,
    val TktPriority: String,
    val Tktdt: String,
    val Tktno: String,
    val isTicketAccepted: Boolean
)