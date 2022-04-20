package com.goldmedal.crm.util.interfaces

import com.goldmedal.crm.data.model.GetAllAssignedTicketsData

public interface AcceptRejectTicketsListener {
    fun onAcceptTicket(data: GetAllAssignedTicketsData)
    fun onRejectTicket(data: GetAllAssignedTicketsData)
}