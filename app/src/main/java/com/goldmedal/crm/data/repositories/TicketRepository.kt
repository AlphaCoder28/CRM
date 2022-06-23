package com.goldmedal.crm.data.repositories

import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.db.entities.TicketHistoryData
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.SafeApiRequest
import com.goldmedal.crm.data.network.responses.*
import org.json.JSONArray

class TicketRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {


    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()


    suspend fun getPincodeWiseStateDistrict(
        userId: Int,
        strPincode: String
    ): PincodeWiseStateDistrictResponse {
        return apiRequest { api.getPincodeWiseStateDistrict(userId, strPincode) }
    }


    suspend fun getAcceptedTicketData(
        userID: Int,
        fromDate: String,
        toDate: String,
        statusBy: Int,
        searchBy: String
    ): GetAcceptedTicketResponse {
        return apiRequest {
            api.getAcceptedTicketList(
                userID,
                fromDate,
                toDate,
                statusBy,
                searchBy
            )
        }
    }


    suspend fun getTicketDetailsForEngineer(
        userID: Int,
        fromDate: String,
        toDate: String,
        type: Int,
        searchBy: String
    ): GetTicketDetailsForEngineerResponse {
        return apiRequest {
            api.getTicketDetailsForEngineer(
                userID,
                fromDate,
                toDate,
                type,
                searchBy
            )
        }
    }

    suspend fun getTicketDetails(userID: Int, ticketId: Int): GetTicketDetailsResponse {
        return apiRequest { api.getTicketDetails(userID, ticketID = ticketId) }
    }

    suspend fun scanQrCode(
        slNoKey: String,
        qrCode: String,
        customerId: Int,
        master: Boolean
    ): QrResponse {
        return apiRequest { api.scanQrCode(slNoKey, qrCode, customerId, master) }
    }

    suspend fun searchQrCode(
        qrCode: String,
        customerId: Int,
        type: Int,
        ticketId: Int,
        master: Boolean
    ): QrResponse {
        return apiRequest { api.searchQrCode(qrCode, customerId, type, ticketId, master) }
    }

    suspend fun getProductSymptomsList(categoryId: Int): ProductSymptomsResponse {
        return apiRequest { api.getProductSymptomsList(categoryId) }
    }


    suspend fun sendClosedOTP(
        strMobileNo: String,
        intTicketId: Int,
        strTicketNo: String,
        intUserId: Int,
        strCustName: String,
        intResendOtp: Int,
        strDeviceId: String

    ): ClosedOtpResponse {
        return apiRequest {
            api.sendClosedOtp(
                strMobileNo = strMobileNo,
                intTicketId = intTicketId,
                strTicketNo = strTicketNo,
                intUserId = intUserId,
                strCustName = strCustName,
                resendOtp = intResendOtp,
                strDeviceId = strDeviceId
            )
        }
    }

    suspend fun getTimeSlots(): TimeSlotResponse {
        return apiRequest { api.getTimeSlot() }
    }


    suspend fun checkIn(
        userID: Int,
        ticketId: Int,
        latitude: String,
        longitude: String,
        location: String,
        isGeofenceLock: Boolean
    ): AcceptRejectResponse {
        return apiRequest {
            api.checkIn(
                userID,
                ticketID = ticketId,
                latitude = latitude,
                longitude = longitude,
                location = location,
                isGeofenceLock = isGeofenceLock
            )
        }
    }

    suspend fun updateTicketStatus(
        userId: Int,
        ticketId: Int,
        actionId: Int,
        divisionId: Int,
        categoryId: Int,
        productId: Int,
        productQRCode: String,
        productEANNo: String,
        productEANNoRemark: String,
        isWarranty: Boolean,
        billProofImg: String,
        dateOfPurchase: String,
        productSymptoms: String,
        rescheduleDate: String,
        rescheduleTimeSlotId: Int,
        rescheduleReason: String,
        updateStatusRemark: String,
        serviceCharge: Int,
        latitude: String,
        longitude: String,
        location: String,
        productImage: String,
        qrImage: String,
        selfieImage: String,
        isGoodFeedback: String,
        isOutOfPremises: Boolean,
        strOutOfPremisesRemark: String,
        strCheckoutDistance: String,
        isNorepair: Boolean,
        isProductReplaced: Boolean,
        replacementQRCode: String
    ): UpdateVisitStatusResponse {
        return apiRequest {
            api.updateVisitStatus(
                userID = userId,
                ticketID = ticketId,
                actionID = actionId,
                divisionID = divisionId,
                categoryID = categoryId,
                productID = productId,
                strItemQRCode = productQRCode,
                strItemEANNo = productEANNo,
                strItemEANNoRemark = productEANNoRemark,
                isWarranty = isWarranty,
                strBillProofImg = billProofImg,
                strPurchaseDate = dateOfPurchase,
                strProductSymptoms = productSymptoms,
                strRescheduleDate = rescheduleDate,
                timeSlotId = rescheduleTimeSlotId,
                strRescheduleReason = rescheduleReason,
                strUpdateStatusRemark = updateStatusRemark,
                serviceCharge = serviceCharge,
                latitude = latitude,
                location = location,
                longitude = longitude,
                productImage = productImage,
                qrImage = qrImage,
                selfieImage = selfieImage,
                isGoodFeedback = isGoodFeedback,
                isOutOfPremises = isOutOfPremises,
                outPremisesRemark = strOutOfPremisesRemark,
                checkOutDistance = strCheckoutDistance,
                isNoRepair = isNorepair,
                isProductReplaced = isProductReplaced,
                replacementQRCode = replacementQRCode
            )
        }
    }


    suspend fun rejectTicket(
        userId: Int,
        ticketId: Int,
        reasonId: Int,
        strPincode: String,
        stateId: Int,
        districtId: Int,
        strCity: String,
        strAddressLine1: String,
        strAddressLine2: String,
        strAddressLine3: String,
        strRemark: String,
        type: Int,
        latitude: String,
        longitude: String,
        location: String
    ): AcceptRejectResponse {
        return apiRequest {
            api.rejectTicket(
                userId,
                ticketId,
                reasonId,
                strPincode,
                stateId,
                districtId,
                strCity,
                strAddressLine1,
                strAddressLine2,
                strAddressLine3,
                strRemark,
                type,
                latitude,
                longitude,
                location
            )
        }
    }

    suspend fun getTicketActivity(ticketId: Int): TicketActivityResponse {
        return apiRequest { api.getTicketActivity(ticketId) }
    }


    /*  - - - - - - - - - - - - -   CUSTOMER PRODUCTS - - - - - - - - - - - -  */
    suspend fun getCustomerProducts(customerId: Int, searchBy: String): CustomerProductsResponse {
        return apiRequest { api.getCustomerProducts(customerId, searchBy) }
    }


    /*  - - - - - - - - - - - - -   TICKETS BY PRODUCTS - - - - - - - - - - - -  */
    suspend fun getTicketsByProducts(customerId: Int, productId: Int): TicketsByProductsResponse {
        return apiRequest { api.getTicketsByProducts(customerId, productId) }
    }


    /*  - - - - - - - - - - - - -   TICKET HISTORY - - - - - - - - - - - -  */
    suspend fun getTicketHistory(userID: Int): TicketHistoryResponse {
        return apiRequest { api.getTicketHistory(userID) }
    }

    suspend fun saveTicketHistory(historyData: List<TicketHistoryData?>) =
        db.getTicketHistoryDao().insertTicketHistory(historyData)

    fun getTicketHistory() = db.getTicketHistoryDao().getTicketHistory()

    suspend fun removeTicketHistory() = db.getTicketHistoryDao().removeTicketHistory()


    /*  - - - - - - - - - - - - -   GET INVOICE ITEM DETAIL - - - - - - - - - - - -  */

    suspend fun getInvoiceItemDetails(
        searchBy: String,
        userID: Int,
        custID: Int,
        tktID: Int,
        scanType: Int,
        qrCode: String,
        productID: Int
    ): GetItemForInvoiceResponse {
        return apiRequest {
            api.getInvoiceItemDetail(
                searchBy,
                userID,
                custID,
                tktID,
                scanType,
                qrCode,
                productID
            )
        }
    }


    /*  - - - - - - - - - - - - -   GET INVOICE HISTORY DETAIL - - - - - - - - - - - -  */

    suspend fun getInvoiceHistoryDetails(userID: Int, searchBy: String): GetInvoiceListResponse {
        return apiRequest { api.getInvoiceList(userID, searchBy) }
    }

    /*  - - - - - - - - - - - - -   GET EDIT INVOICE DETAIL - - - - - - - - - - - -  */

    suspend fun getEditInvoiceDetails(invoiceID: Int): EditInvoiceResponse {
        return apiRequest { api.getEditInvoiceData(invoiceID) }
    }

    /*  - - - - - - - - - - - - -   UPDATE INVOICE DETAIL  - - - - - - - - - - - -  */

    suspend fun updateInvoiceDetails(
        slNo: Int,
        paymentMethod: String,
        paymentStatus: Boolean,
        status: String,
        userID: Int
    ): UpdateInvoiceResponse {
        return apiRequest {
            api.updateInvoiceData(
                slNo,
                paymentMethod,
                paymentStatus,
                status,
                userID
            )
        }
    }


    /*  - - - - - - - - - - - - -   GENERATE INVOICE FOR ITEMS - - - - - - - - - - - -  */

    suspend fun generateInvoiceForItems(
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
    ): UpdateVisitStatusResponse {
        return apiRequest {
            api.generateInvoiceForItems(
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
        }
    }


}