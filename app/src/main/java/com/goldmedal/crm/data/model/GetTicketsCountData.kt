package com.goldmedal.crm.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetTicketsCountData(
    val TicketId: Int?,
    val TicketName: String?,
    val TicketCount: Int?
) : Parcelable