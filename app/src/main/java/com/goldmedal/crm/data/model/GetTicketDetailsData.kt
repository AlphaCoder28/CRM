package com.goldmedal.crm.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetTicketDetailsData(
    val AssignRemark: String?,
    val City: String?,
    val CustAddress: String?,
    val CustContactNo: String?,
    val CustName: String?,
    val Distrctnm: String?,
    val DivisionName: String?,
    val DueByDate: String?,
    val EmailID: String?,
    val EngineerInstructions: String?,
    val InWarranty: Boolean?,
    val IsCheckedIn: Int?,
    val IsProductWarnty: String?,
    val IsSCAddressverified: Boolean?,
    val ItemEANNo: String?,
    val ItemQRCode: String?,
    val ItemSerialNo: String?,
    val PartyAddress: String?,
    val PartyName: String?,
    val PartyTypeName: String?,
    val Pincode: String?,
    val ProductIssueDesc: String?,
    val ProductIssues: String?,
    val ProductName: String?,
    val PurchaseDt: String?,
    val TicketDate: String?,
    val TicketID: Int?,
    val TicketNo: String?,
    val TicketPriority: String?,
    val TicketStatus: String?,
    val TimeSlot: String?,
    val WarrantyUptoDate: String?,
    val isFreeService: Boolean?,
    val statenm: String?,
    val DivisionID: Int?,
    val CategoryID: Int?,
    val ProductID: Int?,
    val AppointmentDate : String?,
    val isGeoFenceLock : Boolean?,
    val ManufactureDate : String?,
    val ReScheduleDate : String?,
    val ReScheduleRemark : String?,
    val ReScheduledByName : String?,
    val BillPhotoProof : String?,
    val SelfieImage : String?,
    val QRImage : String?,
    val ProductImage : String?,
    val CheckinLat : String?,
    val CheckinLong : String?,
    val CustomerID: Int?,
    val IsInvoiceGenrated: Boolean,
    val InvoicePDF: String?,
    val IsDealerCall: Boolean,
    val Type: Int?,
    val IsNoRepair:Boolean,
    val IsNewSymptomsBind: Boolean

) : Parcelable

