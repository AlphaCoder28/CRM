package com.goldmedal.crm.ui.dashboard.home


import androidx.lifecycle.ViewModel
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.data.repositories.HomeRepository
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException
import java.net.SocketTimeoutException


class HomeViewModel(
        private val repository: HomeRepository

) : ViewModel() {

    fun getLoggedInUser() = repository.getLoggedInUser()
//    fun getBirthDataDetail() = repository.getBirthData()
//    fun getAnniversaryDataDetail() = repository.getAnniversaryData()
//    fun getHolidayDataDetail() = repository.getHolidaysData()
//    fun getAllHolidayDataDetail() = repository.getAllHolidaysData()
//    fun getEmployeeAttendanceData() = repository.getEmpAttendanceData()

    var apiListener: DashboardApiListener<Any>? = null



    fun getTicketsCount(userId: Int?,fromDate: String,toDate: String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "tickets_cnt")
            return
        }

        apiListener?.onStarted("tickets_cnt")

        Coroutines.main {
            try {
                val ticketsCntResponse = repository.getTicketsCount(userId,fromDate, toDate)
                if (!ticketsCntResponse.ticketsCount?.isNullOrEmpty()!!) {
                    ticketsCntResponse.ticketsCount.let {
                        apiListener?.onSuccess(it, "tickets_cnt",ticketsCntResponse.Timestamp ?: "")
                        return@main
                    }
                }else {
                    val errorResponse = ticketsCntResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "tickets_cnt", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "tickets_cnt", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "tickets_cnt", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "tickets_cnt", true)
            }
        }
    }




    fun getAppointments(userId: Int?, fromDate: String, toDate: String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "today_appointment")
            return
        }

        apiListener?.onStarted("today_appointment")

        Coroutines.main {
            try {
                val todayAppointment = repository.getAppointments(userId,fromDate, toDate)
                if (!todayAppointment.todayAppointment?.isNullOrEmpty()!!) {
                    todayAppointment.todayAppointment.let {
                        apiListener?.onSuccess(it, "today_appointment",todayAppointment.Timestamp ?: "")
                        return@main
                    }
                }else {
                    val errorResponse = todayAppointment.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "today_appointment", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "today_appointment", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "today_appointment", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "today_appointment", true)
            }
        }
    }



    fun getDashboardData(userID: Int?) {


        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "dashboard")
            return
        }

        apiListener?.onStarted("dashboard")

        Coroutines.main {
            try {
                val getDashboardResponse = repository.getDashboardData(userID)

                getDashboardResponse.data?.let {
                    apiListener?.onSuccess(it, "dashboard", getDashboardResponse.servertime ?: "")
                    return@main
                }

                val errorResponse = getDashboardResponse.Errors
                if (!errorResponse?.isNullOrEmpty()!!) {
                    errorResponse[0]?.ErrorMsg?.let {
                        apiListener?.onError(it, "dashboard", false)
                    }
                }
                            //    apiListener?.onError("Error","dashboard",false)

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!,"dashboard",true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!,"dashboard",true)
            }
        }

    }


    fun getAllAssignedTickets(userId: Int?) {

 if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "all_assigned_tickets")
            return
        }

        apiListener?.onStarted("all_assigned_tickets")

        Coroutines.main {
            try {
                val allAssignedTktsResponse = repository.getAllAssignedTickets(userId)

                if (!allAssignedTktsResponse.allAssignedTkts?.isNullOrEmpty()!!) {
                    allAssignedTktsResponse.allAssignedTkts.let {
                        apiListener?.onSuccess(it, "all_assigned_tickets",allAssignedTktsResponse.servertime ?: "")

                       // repository.removeHolidaysData()
                      //  repository.saveHolidaysData(it)

                        return@main
                    }
                }else {



                    val errorResponse = allAssignedTktsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {


                            apiListener?.onError(it, "all_assigned_tickets", false)
                        }
                    }
                }



            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "all_assigned_tickets", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "all_assigned_tickets", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "all_assigned_tickets", true)


            }
        }

    }

//    "TicketID": 83,
//    2021-03-31 12:16:21.568 15514-16436/com.goldmedal.crm D/OkHttp:       "Tktno": "MH/21012021/151043",

    fun acceptTicket(userId: Int?,ticketNo: Int,latitude: String,longitude: String,location: String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "accept_tkt")
            return
        }

        else if (ticketNo == -1) {
            apiListener?.onValidationError("Ticket id cannot be nil", "accept_tkt")
            return
        }

        apiListener?.onStarted("accept_tkt")

        Coroutines.main {
            try {
                val rejectResponse =  repository.acceptTicket(userId!!, ticketNo!!, latitude, longitude, location)

                if (rejectResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!rejectResponse?.acceptRejectTkt?.isNullOrEmpty()!!) {
                        rejectResponse.acceptRejectTkt.let {
                            apiListener?.onSuccess(it, "accept_tkt",rejectResponse.Timestamp ?: "")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = rejectResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "accept_tkt", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "accept_tkt", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "accept_tkt", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "accept_tkt", true)
            }
        }

    }



    fun getCustomerContacts(userId: Int?,searchBy: String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "contacts")
            return
        }

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("contacts")

        Coroutines.main {
            try {
                val contactsResponse = repository.getCustomerContacts(userId,strSearchBy)

                if (!contactsResponse.contactsData?.isNullOrEmpty()!!) {
                    contactsResponse.contactsData.let {
                        apiListener?.onSuccess(it, "contacts",contactsResponse.Timestamp ?: "")
                        return@main
                    }
                }else {

                    val errorResponse = contactsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "contacts", false)
                        }
                    }
                }



            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "contacts", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "contacts", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "contacts", true)


            }
        }

    }

//    fun upcomingBirthdays(userId: Int?) {
//
//        apiListener?.onStarted("birthday")
//
//        Coroutines.main {
//            try {
//                val birthdayResponse = userId?.let { repository.upcomingBirthdays(it) }
//
//                if (!birthdayResponse?.birthdayData?.isNullOrEmpty()!!) {
//                    birthdayResponse.birthdayData.let {
//                        apiListener?.onSuccess(it, "birthday")
//                        Log.d("Inside", "Msg - - - -" + it.size)
//                        println("inside - - - - - -  - --  - - - - - -")
//                        repository.removeBirthData()
//                        repository.saveBirthData(it)
//                        print("inside - - - " + it.size)
//                        return@main
//                    }
//                }
//
//                apiListener?.onError(birthdayResponse.StatusCodeMessage!!, "birthday")
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "birthday")
//            } catch (e: NoInternetException) {
//                apiListener?.onError(e.message!!, "birthday")
//
//            }catch (e: SocketTimeoutException) {
//                Log.d("NEW EXCEPTION",e.toString())
//                apiListener?.onError(e.message!!, "birthday")
//            }
//        }
//
//    }


//    fun upcomingAnniversary(userId: Int?) {
//
//        apiListener?.onStarted("anniversary")
//
//        Coroutines.main {
//            try {
//                val anniversaryResponse = userId?.let { repository.upcomingAnniversary(it) }
//
//                if (!anniversaryResponse?.anniversaryData?.isNullOrEmpty()!!) {
//                    anniversaryResponse.anniversaryData.let {
//                        apiListener?.onSuccess(it, "anniversary")
//                        Log.d("Inside anniversary", "Msg - - - -" + it.size)
//                        println("inside anniversary - - - - - -  - --  - - - - - -")
//                        repository.removeAnniversaryData()
//                        repository.saveAnniversaryData(it)
//                        print("inside - - - " + it.size)
//                        return@main
//                    }
//                }
//
//                apiListener?.onError(anniversaryResponse.StatusCodeMessage!!, "anniversary")
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "anniversary")
//            } catch (e: NoInternetException) {
//
//                apiListener?.onError(e.message!!, "anniversary")
//                print("Internet not available")
//            }catch (e: SocketTimeoutException) {
//                Log.d("NEW EXCEPTION",e.toString())
//                apiListener?.onError(e.message!!, "anniversary")
//            }
//        }
//
//    }
//
//
//    fun employeeAttendance(userId: Int?) {
//
//        apiListener?.onStarted("employee_attendance")
//
//        Coroutines.main {
//            try {
//                val employeeAttendanceResponse = userId?.let { repository.employeeAttendance(it) }
//
//                if (!employeeAttendanceResponse?.employeeAttendanceData?.isNullOrEmpty()!!) {
//                    employeeAttendanceResponse.employeeAttendanceData.let {
//                        apiListener?.onSuccess(it, "employee_attendance")
//
//                       repository.removeEmpAttendanceData()
//                        repository.saveEmployeeAttendance(it)
//
//                        return@main
//                    }
//                }
//
//                apiListener?.onError(employeeAttendanceResponse.StatusCodeMessage!!, "employee_attendance")
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "employee_attendance")
//            } catch (e: NoInternetException) {
//
//                apiListener?.onError(e.message!!, "employee_attendance")
//                print("Internet not available")
//            }catch (e: SocketTimeoutException) {
//                Log.d("NEW EXCEPTION",e.toString())
//                apiListener?.onError(e.message!!, "employee_attendance")
//            }
//        }
//
//    }
//
//
//
//
//
//
//
//
//
//
//    fun punchInoutStatus(userId: Int?) {
//
//        apiListener?.onStarted("punchStatus")
//
//        Coroutines.main {
//            try {
//                val punchStatusResponse = userId?.let { repository.punchInoutStatus(it) }
//
//                if (punchStatusResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
//                    if (!punchStatusResponse?.punchStatusData?.isNullOrEmpty()!!) {
//                        punchStatusResponse.punchStatusData.let {
//                            apiListener?.onSuccess(it, "punchStatus")
//                            return@main
//                        }
//                    }
//                } else {
//                    val errorResponse = punchStatusResponse?.Errors
//                    if (!errorResponse?.isNullOrEmpty()!!) {
//                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "punchStatus") }
//
//
//                    }
//                }
//
//                // apiListener?.onFailure(punchStatusResponse.StatusCodeMessage!!,"holidays")
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "punchStatus")
//            } catch (e: NoInternetException) {
//
//                apiListener?.onError(e.message!!, "punchStatus")
//                print("Internet not available")
//            }catch (e: SocketTimeoutException) {
//                Log.d("NEW EXCEPTION",e.toString())
//                apiListener?.onError(e.message!!, "punchStatus")
//            }
//        }
//
//    }


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
                        apiListener?.onSuccess(it, "ticket_details",ticketDetailsResponse.Timestamp ?: "")
                        return@main
                    }
                } else {


                    val errorResponse = ticketDetailsResponse?.Errors
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
                        apiListener?.onSuccess(it, "service_tickets", allTktsResponse.Timestamp ?: "")
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


    fun getManagerTicketData(userId: Int,serviceId:Int) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil","")
            return
        }

        apiListener?.onStarted("manager_ticket_count")

        Coroutines.main {
            try {
                val managerTicketResponse = repository.managerTicketCount(userId,serviceId)

                if (!managerTicketResponse?.Data?.isNullOrEmpty()!!) {
                    managerTicketResponse?.Data?.let {

                        apiListener?.onSuccess(it, "manager_ticket_count","")

                    }
                } else {
                    apiListener?.onError("", "manager_ticket_count", false)
//                    val errorResponse = //managerTicketResponse.Errors
//                    if (!errorResponse?.isNullOrEmpty()!!) {
//                        errorResponse[0]?.ErrorMsg?.let {
//                            apiListener?.onError(it, "manager_ticket_count", false)
//                        }
//                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "manager_ticket_count", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "manager_ticket_count", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "manager_ticket_count", true)
            }
        }
    }


    fun getManagerMonthwise(userId: Int,serviceId:Int) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil","")
            return
        }

        apiListener?.onStarted("")

        Coroutines.main {
            try {
                val managerMonthwiseTicketResponse = repository.managerMonthwiseTicket(userId,serviceId)

                if (!managerMonthwiseTicketResponse?.Data?.isNullOrEmpty()!!) {
                    managerMonthwiseTicketResponse?.Data?.let {

                        apiListener?.onSuccess(it, "manager_ticket_monthwise_count","")

                    }
                } else {
                    apiListener?.onError("", "manager_ticket_monthwise_count", false)
//                    val errorResponse = //managerTicketResponse.Errors
//                    if (!errorResponse?.isNullOrEmpty()!!) {
//                        errorResponse[0]?.ErrorMsg?.let {
//                            apiListener?.onError(it, "manager_ticket_count", false)
//                        }
//                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "manager_ticket_monthwise_count", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "manager_ticket_monthwise_count", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "manager_ticket_monthwise_count", true)
            }
        }
    }
}
