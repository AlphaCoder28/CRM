package com.goldmedal.crm.ui.dashboard.Manager

import com.goldmedal.crm.data.model.ErrorData

data class ManagerTicketCountData(
    val Data: List<Data>?,
    val Errors: List<ErrorData?>?,
    val Size: Int?,
    val StatusCode: Int?,
    val StatusCodeMessage: String?,
    val Timestamp: String?,
    val Version: String?
)

data class Data(
    val AssignedTickets: Int?,
    val ClosedTickets: Int?,
    val NotAssignedTickets: Int?,
    val OpenTickets: Int?,
    val PendingTickets: Int?,
    val ProcessedTickets: Int?,
    val ReassignTickets: Int?,
    val RejectedTickets: Int?,
    val RejectedTicketsse: Int?,
    val TotalTickets: Int?
)