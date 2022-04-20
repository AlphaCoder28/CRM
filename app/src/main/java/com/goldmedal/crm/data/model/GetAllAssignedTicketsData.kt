package com.goldmedal.crm.data.model

import com.goldmedal.crm.data.network.GlobalConstant

data class GetAllAssignedTicketsData(
        val CustAddress: String? = null,
        val ProductIssues: String? = null,
        val ProductName: String? = null,
        val TicketID: Int? = null,
        val TimeSlot: String? = null,
        val TktPriority: String? = null,
        val Tktno: String? = null,
        val CustName: String? = null,
    //    var ViewType: Int = GlobalConstant.TYPE_NO_DATA,
        val CustContactNo: String? = null,
        val AppointmentDate: String? = null
){


//     constructor() : this("", "",
//
//            "", -1,"","","","", 1001,
//            "",""
//    )
}