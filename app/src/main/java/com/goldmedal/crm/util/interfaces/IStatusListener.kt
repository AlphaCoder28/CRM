package com.goldmedal.crm.util.interfaces

import com.goldmedal.crm.data.model.GetAcceptedTicketData


interface IStatusListener {
    fun observeCheckInStatus(ticketId: Int?)
    fun cancelTicket(ticketData: GetAcceptedTicketData?,actionId:Int)
}