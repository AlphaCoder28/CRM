package com.goldmedal.crm.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TicketHistoryData(
    val ClosedTicket: Int,
    val Month: String,
    val TotalTicket: Int
){
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}