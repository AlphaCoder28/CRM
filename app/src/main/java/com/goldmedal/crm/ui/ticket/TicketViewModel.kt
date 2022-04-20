package com.goldmedal.crm.ui.ticket

import android.util.Log
import androidx.lifecycle.ViewModel
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.data.repositories.TicketRepository
import com.goldmedal.crm.util.*
import org.json.JSONArray
import java.net.SocketTimeoutException

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null

    var strQrCode: String? = "-"
    var master: Boolean? = false
    var strStatus: String? = ""
    var strSerialNo: String? = null
    var strSerialNoRemark: String? = null
    var strProductSymptoms: String? = ""
    var strBillImage: String? = ""
    var strReplacementImage: String? = ""
    var strProductImage: String? = ""
    var strQRImage: String? = ""
    var strSelfieImage: String? = ""
    var strDateOfPurchase: String? = "01-01-1900"
    var strRescheduleDate: String? = "01-01-1900"
    var timeSlotId: Int? = -1
    var strRescheduleReason: String? = "-"
    var strUpdateStatusRemarks: String? = null
    var inWarranty: Boolean? = false
    var isSlNoAvailable: Boolean? = true
    var actionId: Int = -1
    var divisionId: Int? = null
    var categoryId: Int? = null
    var productId: Int? = null
    var serviceCharge: Int? = 0
    var serviceOTP: String? = "-"

    //F011120381000005
    fun getLoggedInUser() = repository.getLoggedInUser()
    fun getTicketHistoryDetail() = repository.getTicketHistory()

    fun getAcceptedTickets(
        userId: Int?,
        fromDate: String,
        toDate: String,
        statusBy: Int,
        searchBy: String
    ) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "accepted_tickets")
            return
        }

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("accepted_tickets")

        Coroutines.main {
            try {
                val allTktsResponse = repository.getAcceptedTicketData(
                    userId,
                    fromDate,
                    toDate,
                    statusBy,
                    strSearchBy
                )

                if (!allTktsResponse.acceptedTickets?.isNullOrEmpty()!!) {
                    allTktsResponse.acceptedTickets.let {
                        apiListener?.onSuccess(it, "accepted_tickets")
                        return@main
                    }
                } else {


                    val errorResponse = allTktsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "accepted_tickets", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "accepted_tickets", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "accepted_tickets", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "accepted_tickets", true)


            }
        }

    }


    fun getTicketDetailsForEngineer(
        userId: Int?,
        fromDate: String,
        toDate: String,
        statusBy: Int,
        searchBy: String
    ) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "service_tickets")
            return
        }

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("service_tickets")

        Coroutines.main {
            try {
                val allTktsResponse = repository.getTicketDetailsForEngineer(
                    userId,
                    fromDate,
                    toDate,
                    statusBy,
                    strSearchBy
                )

                if (!allTktsResponse.ticketDetails?.isNullOrEmpty()!!) {
                    allTktsResponse.ticketDetails.let {
                        apiListener?.onSuccess(it, "service_tickets")
                        return@main
                    }
                } else {
                    val errorResponse = allTktsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "service_tickets", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "service_tickets", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "service_tickets", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "service_tickets", true)


            }
        }

    }


    fun getTicketDetails(userId: Int?, ticketId: Int) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
            return
        }
        if (ticketId == -1) {
            apiListener?.onValidationError("ticket id cannot be nil", "ticket_details")
            return
        }

        apiListener?.onStarted("ticket_details")

        Coroutines.main {
            try {
                val ticketDetailsResponse = repository.getTicketDetails(userId, ticketId)

                if (!ticketDetailsResponse.ticketDetails?.isNullOrEmpty()!!) {
                    ticketDetailsResponse.ticketDetails.let {
                        apiListener?.onSuccess(it, "ticket_details")
                        return@main
                    }
                } else {


                    val errorResponse = ticketDetailsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "ticket_details", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "ticket_details", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "ticket_details", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "ticket_details", true)


            }
        }

    }


    fun checkIn(
        userId: Int?,
        ticketId: Int,
        latitude: String,
        longitude: String,
        location: String,
        isGeofenceLock: Boolean
    ) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "check_in")
            return
        }
        if (ticketId == -1) {
            apiListener?.onValidationError("ticket id cannot be nil", "check_in")
            return
        }

        apiListener?.onStarted("check_in")

        Coroutines.main {
            try {
                val checkInResponse =
                    repository.checkIn(
                        userId,
                        ticketId,
                        latitude,
                        longitude,
                        location,
                        isGeofenceLock
                    )

                if (!checkInResponse.acceptRejectTkt?.isNullOrEmpty()!!) {
                    checkInResponse.acceptRejectTkt.let {
                        apiListener?.onSuccess(it, "check_in")
                        return@main
                    }
                } else {

                    val errorResponse = checkInResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "check_in", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "check_in", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "check_in", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "check_in", true)
            }
        }

    }


    fun scanQrCode(slNoKey: String?, qrCode: String?, customerId: Int, master: Boolean) {
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
//            return
//        }
        if (slNoKey == null) {
            apiListener?.onValidationError("slno cannot be nil", "product_info_scan")
            return
        }
        if (qrCode == null) {
            apiListener?.onValidationError("Qr Code cannot be nil", "product_info_scan")
            return
        }


        apiListener?.onStarted("product_info_scan")

        Coroutines.main {
            try {
                val ticketDetailsResponse =
                    repository.scanQrCode(slNoKey, qrCode, customerId, master)

                if (!ticketDetailsResponse.productInfo?.isNullOrEmpty()!!) {
                    ticketDetailsResponse.productInfo.let {
                        apiListener?.onSuccess(it, "product_info_scan")
                        return@main
                    }
                } else {
                    val errorResponse = ticketDetailsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {


                            apiListener?.onError(it, "product_info_scan", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "product_info_scan", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "product_info_scan", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "product_info_scan", true)


            }
        }

    }

    fun searchQrCode(qrCode: String, customerId: Int, type: Int, ticketID: Int, master: Boolean) {
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
//            return
//        }
        apiListener?.onStarted("product_info_search")

        Coroutines.main {
            try {
                val ticketDetailsResponse =
                    repository.searchQrCode(qrCode, customerId, type, ticketID, master)

                if (!ticketDetailsResponse.productInfo?.isNullOrEmpty()!!) {
                    ticketDetailsResponse.productInfo.let {
                        apiListener?.onSuccess(it, "product_info_search")
                        return@main
                    }
                } else {
                    val errorResponse = ticketDetailsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "product_info_search", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "product_info_search", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "product_info_search", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "product_info_search", true)


            }
        }

    }


    fun getSymptomsList(categoryId: Int?) {
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
//            return
//        }
        if (categoryId == null) {
            apiListener?.onValidationError("category cannot be nil", "product_symptoms_list")
            return
        }



        apiListener?.onStarted("product_symptoms_list")

        Coroutines.main {
            try {

                val productSymptomsResponse = repository.getProductSymptomsList(categoryId)

                if (!productSymptomsResponse.symptomsList?.isNullOrEmpty()!!) {
                    productSymptomsResponse.symptomsList.let {
                        apiListener?.onSuccess(it, "product_symptoms_list")
                        return@main
                    }
                } else {
                    val errorResponse = productSymptomsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {


                            apiListener?.onError(it, "product_symptoms_list", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "product_symptoms_list", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "product_symptoms_list", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "product_symptoms_list", true)


            }
        }

    }


    fun getTimeSlots() {
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
//            return
//        }


        apiListener?.onStarted("time_slots")

        Coroutines.main {
            try {
                val productSymptomsResponse = repository.getTimeSlots()

                if (!productSymptomsResponse.timeSlots?.isNullOrEmpty()!!) {
                    productSymptomsResponse.timeSlots.let {
                        apiListener?.onSuccess(it, "time_slots")
                        return@main
                    }
                } else {
                    val errorResponse = productSymptomsResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "time_slots", false)
                        }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "time_slots", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "time_slots", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "time_slots", true)
            }
        }

    }


    fun getTicketHistory(userId: Int?) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "ticket_history")
            return
        }
        apiListener?.onStarted("ticket_history")

        Coroutines.main {
            try {
                val ticketHistoryResponse = repository.getTicketHistory(userId)

                if (!ticketHistoryResponse.data?.isNullOrEmpty()!!) {
                    ticketHistoryResponse.data.let {
                        apiListener?.onSuccess(it, "ticket_history")

                        repository.removeTicketHistory()
                        repository.saveTicketHistory(it)
                        return@main
                    }
                } else {
                    if (ticketHistoryResponse.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeTicketHistory()
                    }

                    val errorResponse = ticketHistoryResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "ticket_history", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "ticket_history", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "ticket_history", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "ticket_history", true)


            }
        }

    }


    fun getTicketActivity(ticketId: Int) { //userId: Int?
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "ticket_details")
//            return
//        }
        if (ticketId == -1) {
            apiListener?.onValidationError("ticket id cannot be nil", "ticket_activity")
            return
        }

        apiListener?.onStarted("ticket_activity")

        Coroutines.main {
            try {
                val ticketActivityResponse = repository.getTicketActivity(ticketId) //userId,

                if (!ticketActivityResponse.tktActivity?.isNullOrEmpty()!!) {
                    ticketActivityResponse.tktActivity.let {
                        apiListener?.onSuccess(it, "ticket_activity")
                        return@main
                    }
                } else {
                    val errorResponse = ticketActivityResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "ticket_activity", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "ticket_activity", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "ticket_activity", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "ticket_activity", true)
            }
        }
    }


    fun getOtpForCloseTicket(
        strMobileNo: String,
        intTicketId: Int,
        strTicketNo: String,
        intUserId: Int?,
        strCustName: String,
        latitude: String,
        longitude: String,
        location: String,
        isOutOfPremises: Boolean,
        strOutOfPremisesRemark: String,
        strCheckoutDistance: String,
        purchaseDate: Boolean,
        isNoRepair: Boolean,
        isProductReplaced: Boolean,
        intResendOtp: Int,
        strDeviceId: String
    ) {

        if (intUserId == null) {
            apiListener?.onValidationError("User id cannot be nil", "close_otp")
            return
        }

        if (intTicketId == -1) {
            apiListener?.onValidationError("ticket id cannot be nil", "close_otp")
            return
        }

        if (categoryId == null) {
            apiListener?.onValidationError("Category id cannot be nil", "close_otp")
            return
        }

        if (divisionId == null) {
            apiListener?.onValidationError("Division id cannot be nil", "close_otp")
            return
        }

        if (productId == null) {
            apiListener?.onValidationError("Product id cannot be nil", "close_otp")
            return
        }

        if (strUpdateStatusRemarks.isNullOrEmpty()) {
            apiListener?.onValidationError("Enter Engineer Remark", "close_otp")
            return
        }

        if (strSerialNo.isNullOrEmpty()) {
            strSerialNo = "-"
        }

        if (strSerialNoRemark.isNullOrEmpty()) {
            strSerialNoRemark = "-"
        }

        if (strDateOfPurchase.isNullOrEmpty()) {
            strDateOfPurchase = "01-01-1900"
        }

        when (actionId) {
            //Re-Assign
            1 -> {
                updateTicketStatus(
                    intUserId,
                    intTicketId,
                    latitude,
                    longitude,
                    location,
                    isOutOfPremises,
                    strOutOfPremisesRemark,
                    strCheckoutDistance,
                    isNoRepair,
                    isProductReplaced
                )
                return
            }
            //Re Schedule
            2 -> {
                if (strRescheduleDate.equals("01-01-1900")) {
                    apiListener?.onValidationError("Select Re-Schedule Date", "close_otp")
                    return
                }
                if (timeSlotId == -1) {
                    apiListener?.onValidationError("Select Time Slot", "close_otp")
                    return
                }
                updateTicketStatus(
                    intUserId,
                    intTicketId,
                    latitude,
                    longitude,
                    location,
                    isOutOfPremises,
                    strOutOfPremisesRemark,
                    strCheckoutDistance,
                    isNoRepair,
                    isProductReplaced
                )
                return
            }
            //Close
            3 -> {
                if (strDateOfPurchase.equals("01-01-1900") && !purchaseDate) {
                    apiListener?.onValidationError("Select Purchase Date", "close_otp")
                    return
                }

                if (!isNoRepair) {

                    if (strProductSymptoms.isNullOrEmpty()) {
                        apiListener?.onValidationError("Select Product Symptoms", "close_otp")
                        return
                    }

                    if (isSlNoAvailable ?: true) {

                    } else {
                        if (strSerialNoRemark.equals("-")) {
                            apiListener?.onValidationError(
                                "Enter Serial Number not available Remark",
                                "close_otp"
                            )
                            return
                        }
                        if (strProductImage.equals("")) {
                            apiListener?.onValidationError(
                                "Please Upload Product Image as Serial Number is not provided",
                                "close_otp"
                            )
                            return
                        }

                    }
                }


                if (isProductReplaced) {
                    if (strReplacementImage.equals("")) {
                        apiListener?.onValidationError(
                            "Please Upload Replacement Image",
                            "close_otp"
                        )
                        return
                    }
                }

            }
            else -> {
                apiListener?.onValidationError("Select Visit Status", "close_otp")
                return
            }
        }


        apiListener?.onStarted("close_otp")

        Coroutines.main {
            try {
                val closeOtpResponse = repository.sendClosedOTP(
                    strMobileNo!!,
                    intTicketId!!,
                    strTicketNo!!,
                    intUserId!!,
                    strCustName!!,
                    intResendOtp!!,
                    strDeviceId!!
                )

                if (!closeOtpResponse.closedOtp?.isNullOrEmpty()!!) {
                    closeOtpResponse.closedOtp.let {
                        apiListener?.onSuccess(it, "close_otp")
                        return@main
                    }
                } else {
                    val errorResponse = closeOtpResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "close_otp", false)
                        }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "close_otp", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "close_otp", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "close_otp", true)
            }
        }

    }


    fun updateTicketStatus(
        userId: Int?,
        ticketId: Int,
        latitude: String,
        longitude: String,
        location: String,
        outOfPremises: Boolean,
        strOutOfPremisesRemark: String,
        strCheckoutDistance: String,
        isNoRepair: Boolean,
        isProductReplaced: Boolean?
    ) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "update_ticket_status")
            return
        }

        if (actionId == 4) {
            if (ticketId == -1) {
                apiListener?.onValidationError("Ticket id cannot be nil", "update_ticket_status")
                return
            }
            if (strRescheduleDate.equals("01-01-1900")) {
                apiListener?.onValidationError("Select Re-Schedule Date", "update_ticket_status")
                return
            }
            if (timeSlotId == -1) {
                apiListener?.onValidationError("Select Time Slot", "update_ticket_status")
                return
            }
            if (strUpdateStatusRemarks.isNullOrEmpty()) {
                apiListener?.onValidationError("Enter Engineer Remark", "update_ticket_status")
                return
            }
        }

        if (strProductSymptoms.isNullOrEmpty()) {
            strProductSymptoms = "0"
        }


        if(strDateOfPurchase?.contains("/") == true) {
            strDateOfPurchase = strDateOfPurchase?.let { formatDateString(it, "dd/MM/YYYY", "MM-dd-YYYY") }
        }


        apiListener?.onStarted("update_ticket_status")

        Coroutines.main {
            try {
                val updateStatusResponse = repository.updateTicketStatus(
                    userId,
                    ticketId,
                    actionId,
                    divisionId ?: 0,
                    categoryId ?: 0,
                    productId ?: 0,
                    strQrCode ?: "-",
                    strSerialNo ?: "-",
                    strSerialNoRemark ?: "-",
                    inWarranty ?: false,
                    strBillImage ?: "-",
                    strDateOfPurchase ?: "01-01-1900",
                    strProductSymptoms ?: "",
                    strRescheduleDate ?: "01-01-1900",
                    timeSlotId ?: 0,
                    strRescheduleReason ?: "-",
                    strUpdateStatusRemarks ?: "-",
                    serviceCharge ?: 0,
                    latitude,
                    longitude,
                    location,
                    strProductImage ?: "",
                    strQRImage
                        ?: "",
                    strSelfieImage ?: "",
                    serviceOTP ?: "-",
                    outOfPremises,
                    strOutOfPremisesRemark,
                    strCheckoutDistance,
                    isNoRepair,
                    isProductReplaced ?: false,
                    strReplacementImage ?: "-"
                )

                if (!updateStatusResponse.updateStatus?.isNullOrEmpty()!!) {
                    updateStatusResponse.updateStatus.let {
                        apiListener?.onSuccess(it, "update_ticket_status")
                        return@main
                    }
                } else {

                    val errorResponse = updateStatusResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "update_ticket_status", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "update_ticket_status", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "update_ticket_status", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "update_ticket_status", true)
            }
        }

    }


    fun getCustomerProducts(customerId: Int, searchBy: String) {

        if (customerId == -1) {
            apiListener?.onValidationError("customer id cannot be nil", "customer_products")
            return
        }



        apiListener?.onStarted("customer_products")

        Coroutines.main {
            try {

                val outputResponse = repository.getCustomerProducts(
                    customerId,
                    if (searchBy.isEmpty()) "-" else searchBy
                )

                if (!outputResponse.data?.isNullOrEmpty()!!) {
                    outputResponse.data.let {
                        apiListener?.onSuccess(it, "customer_products")
                        return@main
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "customer_products", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "customer_products", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "customer_products", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "customer_products", true)
            }
        }

    }


    fun getTicketsByProducts(customerId: Int, productId: Int) {

        if (customerId == -1) {
            apiListener?.onValidationError("customer id cannot be nil", "tickets_by_products")
            return
        }

        if (productId == -1) {
            apiListener?.onValidationError("product id cannot be nil", "tickets_by_products")
            return
        }

        apiListener?.onStarted("tickets_by_products")

        Coroutines.main {
            try {

                val outputResponse = repository.getTicketsByProducts(customerId, productId)
                if (!outputResponse.data?.isNullOrEmpty()!!) {
                    outputResponse.data.let {
                        apiListener?.onSuccess(it, "tickets_by_products")
                        return@main
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "tickets_by_products", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "tickets_by_products", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "tickets_by_products", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "tickets_by_products", true)
            }
        }

    }


    fun getInvoiceItemDetail(
        searchBy: String,
        userID: Int,
        custId: Int,
        tktID: Int,
        scanType: Int,
        qrCode: String,
        productID: Int
    ) {

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("invoice_item")

        Coroutines.main {
            try {
                val invoiceItemResponse = repository.getInvoiceItemDetails(
                    strSearchBy,
                    userID,
                    custId,
                    tktID,
                    scanType,
                    qrCode,
                    productID
                )

                if (!invoiceItemResponse.getItemForInvoice?.isNullOrEmpty()!!) {
                    invoiceItemResponse.getItemForInvoice.let {
                        apiListener?.onSuccess(it, "invoice_item")
                        return@main
                    }
                } else {

                    val errorResponse = invoiceItemResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "invoice_item", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "invoice_item", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "invoice_item", true)


            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "invoice_item", true)


            }
        }

    }


    fun generateInvoiceForItems(
        slNo: Int,
        custId: Int,
        tktId: Int,
        status: String,
        taxType: Int,
        taxAmount1: Double,
        taxAmount2: Double,
        preTaxAmount: Double,
        discount: Double,
        afterDiscountAmount: Double,
        finalTotal: Double,
        userID: Int,
        logNo: Int,
        applicationID: Int,
        invoiceItemDetail: JSONArray
    ) {

        apiListener?.onStarted("invoice_generate")

        Coroutines.main {
            try {
                val invoiceGenerateResponse = repository.generateInvoiceForItems(
                    slNo,
                    custId,
                    tktId,
                    status,
                    taxType,
                    taxAmount1,
                    taxAmount2,
                    preTaxAmount,
                    discount,
                    afterDiscountAmount,
                    finalTotal,
                    userID,
                    logNo,
                    applicationID,
                    invoiceItemDetail
                )

                if (!invoiceGenerateResponse.updateStatus?.isNullOrEmpty()!!) {
                    invoiceGenerateResponse.updateStatus.let {
                        apiListener?.onSuccess(it, "invoice_generate")
                        return@main
                    }
                } else {

                    val errorResponse = invoiceGenerateResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "invoice_generate", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "invoice_generate", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "invoice_generate", true)


            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "invoice_generate", true)


            }
        }

    }


    fun editInvoiceDetail(invoiceID: Int) {

        apiListener?.onStarted("edit_invoice_detail")

        Coroutines.main {
            try {
                val editInvoiceResponse = repository.getEditInvoiceDetails(invoiceID)

                if (!editInvoiceResponse.data?.isNullOrEmpty()!!) {
                    editInvoiceResponse.data.let {
                        apiListener?.onSuccess(it, "edit_invoice_detail")
                        return@main
                    }
                } else {

                    val errorResponse = editInvoiceResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "edit_invoice_detail", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "edit_invoice_detail", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "edit_invoice_detail", true)


            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "edit_invoice_detail", true)


            }
        }

    }


    fun updateInvoiceDetail(
        slNo: Int,
        paymentMethod: String,
        paymentStatus: Boolean,
        status: String,
        userID: Int
    ) {

        apiListener?.onStarted("update_invoice")

        Coroutines.main {
            try {
                val updateInvoiceResponse = repository.updateInvoiceDetails(
                    slNo,
                    paymentMethod,
                    paymentStatus,
                    status,
                    userID
                )

                if (!updateInvoiceResponse.data?.isNullOrEmpty()!!) {
                    updateInvoiceResponse.data.let {
                        apiListener?.onSuccess(it, "update_invoice")
                        return@main
                    }
                } else {

                    val errorResponse = updateInvoiceResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "update_invoice", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "update_invoice", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "update_invoice", true)


            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "update_invoice", true)


            }
        }

    }


    fun getInvoiceListDetail(userid: Int, searchBy: String) {

        apiListener?.onStarted("invoice_history")

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        Coroutines.main {
            try {
                val invoiceListResponse = repository.getInvoiceHistoryDetails(userid, strSearchBy)

                if (!invoiceListResponse.getInvoiceList?.isNullOrEmpty()!!) {
                    invoiceListResponse.getInvoiceList.let {
                        apiListener?.onSuccess(it, "invoice_history")
                        return@main
                    }
                } else {

                    val errorResponse = invoiceListResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "invoice_history", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "invoice_history", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "invoice_history", true)


            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "invoice_history", true)


            }
        }

    }


}
