package com.goldmedal.crm.data.repositories

import android.location.Location
import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.model.AddedInvoiceItemData
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.SafeApiRequest
import com.goldmedal.crm.data.network.responses.*
import com.goldmedal.crm.ui.dashboard.Manager.Data
import com.goldmedal.crm.ui.dashboard.Manager.ManagerMonthWiseData
import com.goldmedal.crm.ui.dashboard.Manager.ManagerTicketCountData
import org.json.JSONArray
import retrofit2.http.Field

class HomeRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()

    suspend fun getDashboardData(userID: Int): DashboardResponse {
        return apiRequest { api.dashboardDetail(userID) }
    }

    /*  - - - - - - - - - - - - -   ALL ASSIGNED TICKETS - - - - - - - - - - - -  */

    suspend fun getAllAssignedTickets(userId: Int): GetAssignedTicketResponse {
        return apiRequest {
            api.getAllAssignedTickets(userId)
        }
    }

    /*  - - - - - - - - - - - - -   Manager Ticket Count - - - - - - - - - - - -  */
    suspend fun managerTicketCount(intUserID: Int,intServiceCenterID:Int): ManagerTicketCountData {
        return apiRequest {
            api.getManagerTicketCount(intUserID, intServiceCenterID)
        }
    }

    suspend fun managerMonthwiseTicket(intUserID: Int,intServiceCenterID:Int): ManagerMonthWiseData {
        return apiRequest {
            api.getManagerDashboard(intUserID, intServiceCenterID)
        }
    }

    /*  - - - - - - - - - - - - -  TICKET COUNT FOR SERVICE ENGINEER - - - - - - - - - - - -  */

    suspend fun getTicketsCount(
        userId: Int,
        fromDate: String,
        toDate: String
    ): GetTicketsCountResponse {
        return apiRequest {
            api.getTicketsCount(userId, fromDate, toDate)
        }
    }
    /*  - - - - - - - - - - - - -  TODAY APPOINTMENT FOR SERVICE ENGINEER - - - - - - - - - - - -  */

    suspend fun getAppointments(
        userId: Int,
        fromDate: String,
        toDate: String
    ): GetAppointmentsResponse {
        return apiRequest {
            api.getAppointments(userId, fromDate, toDate)
        }
    }

    /*  - - - - - - - - - - - - -   ACCEPT TICKET - - - - - - - - - - - -  */

    suspend fun acceptTicket(
        userId: Int,
        ticketNo: Int,
        latitude: String,
        longitude: String,
        location: String
    ): AcceptRejectResponse {
        return apiRequest {
            api.acceptTicket(userId, ticketNo, latitude, longitude, location)
        }
    }

    /*  - - - - - - - - - - - - -   CONTACTS - - - - - - - - - - - -  */

    suspend fun getCustomerContacts(userId: Int, searchBy: String): CustomerContactsResponse {
        return apiRequest {
            api.getCustomerContacts(userId, searchBy)
        }
    }

    /*  - - - - - - - - - - - - -   GET TICKET DETAILS- - - - - - - - - - - -  */

    suspend fun getTicketDetails(userID: Int, ticketId: Int): GetTicketDetailsResponse {
        return apiRequest { api.getTicketDetails(userID, ticketID = ticketId) }
    }


    /*  - - - - - - - - - - - - -   SERVICE TICKET DETAILS- - - - - - - - - - - -  */
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


    /*  - - - - - - - - - - - - -   GET ITEM AND PART DETAILS- - - - - - - - - - - -  */

    suspend fun getItemAndPartDetails(itemSlNo: Int, searchBy: String): UsedItemAndPartResponse {
        return apiRequest { api.getItemAndPartDetails(itemSlNo, searchBy) }
    }


    /*  - - - - - - - - - - - - -   GET PART AND ITEM DETAILS- - - - - - - - - - - -  */

    suspend fun getPartAndItemDetails(partSlNo: Int, searchBy: String): UsedPartAndItemResponse {
        return apiRequest { api.getPartAndItemDetails(partSlNo, searchBy) }
    }

    /*  - - - - - - - - - - - - -   GET PART REQUIREMENT DETAILS- - - - - - - - - - - -  */

    suspend fun getPartRequirementDetails(userID: Int,custID: Int, tktId: Int): GetPartsRequirementResponse {
        return apiRequest { api.getPartRequirementData(userID, custID, tktId) }
    }

    /*  - - - - - - - - - - - - -   GET STOCK PARTS LIST- - - - - - - - - - - -  */

    suspend fun getStockPartsDetails(userID: Int,requestNo:String,searchBy:String): SelectPartListResponse {
        return apiRequest { api.getItemStockParts(userID,requestNo,searchBy) }
    }


    /*  - - - - - - - - - - - - -   GET STOCK PARTS LIST REQUESTED BY  USER  - - - - - - - - - - - -  */

    suspend fun getRequestedPartsByUser(requestNo:String): SelectPartListResponse {
        return apiRequest { api.getPartsRequirementByUser(requestNo) }
    }


    /*  - - - - - - - - - - - - -   SUBMIT ADDED PARTS REQUIREMENT LIST  - - - - - - - - - - - -  */

    suspend fun submitPartsRequirementDetails(userID: Int, tktId: Int, custID: Int,partsAddedDetail: JSONArray): SubmitPartsReqResponse {
        return apiRequest { api.submitPartsRequest(userID, tktId, custID, partsAddedDetail) }
    }


    /*  - - - - - - - - - - - - -   PARTS REQUIREMENT LIST DETAIL - - - - - - - - - - - -  */

    suspend fun getPartsRequirementList(userID: Int): GetRequestPartListResponse {
        return apiRequest { api.getRequestPartData(userID) }
    }


}