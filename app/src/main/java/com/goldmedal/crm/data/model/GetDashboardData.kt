package com.goldmedal.crm.data.model

data class GetDashboardData(
    val AllTicket: Int? = null,
    val InProgressTicket: Int? = null,
    val PendingTicket: Int? = null,
    val UrgentTicket: Int? = null
)