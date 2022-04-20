package com.goldmedal.crm.util.interfaces

import com.goldmedal.crm.data.model.GetAcceptedTicketData


interface UsedPartListener {
    fun itemClicked(slno: Int?,strName: String?,callFrom:String)
}