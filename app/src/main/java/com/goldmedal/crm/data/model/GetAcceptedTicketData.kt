package com.goldmedal.crm.data.model


data class  GetAcceptedTicketData(
    val CustAddress: String? = null,
    val CustName: String? = null,
    val CustContactNo: String? = null,
    val ProductIssues: String? = null,
    val ProductName: String? = null,
    val ReScheduleDate: String? = null,
    val ReScheduleRemark: String? = null,
    val ReScheduledByName: String? = null,
    val TicketID: Int? = null,
    val TimeSlot: String? = null,
    val TktPriority: String? = null,
    val Tktno: String? = null,
    val AppointmentDate: String? = null,
    val TktStatus: String? = null
)
