package com.goldmedal.crm.util.interfaces

import com.goldmedal.crm.data.model.GetTicketDetailsForEngineerData

interface OnServiceTicketClickListener {
    fun onTicketClick(model: GetTicketDetailsForEngineerData?,actionId:Int)
}