package com.goldmedal.crm.data.model

data class GetTicketDetailsForEngineerData(
    val AcceptDate: String,
    val AppointmentDate: String,
    val AssignDate: String,
    val AssignedToName: String,
    val CloseDate: String,
    val ClosedByName: String,
    val CustAddress: String,
    val CustContactNo: String,
    val CustName: String,
    val ProductDivision: String,
    val ProductIssues: String,
    val ProductName: String,
    val ReAssignReason: String,
    val ReAssignRequestByName: String,
    val ReAssignRequestDate: String,
    val ReScheduleDate: String,
    val ReScheduleRemark: String,
    val ReScheduledByName: String,
    val ScName: String,
    val TicketID: Int,
    val TimeSlot: String,
    val TktPriority: String,
    val TktStatus: String,
    val Tktdt: String,
    val Tktno: String,
    val VisitByName: String,
    val VisitDate: String,
    val tktCost: String
)