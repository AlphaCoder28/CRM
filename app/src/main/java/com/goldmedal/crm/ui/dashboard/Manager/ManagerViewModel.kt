package com.goldmedal.crm.ui.dashboard.Manager

import androidx.lifecycle.ViewModel
import com.goldmedal.crm.data.repositories.ManagerRepository
import com.goldmedal.crm.ui.auth.AuthListener
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException
import java.net.SocketTimeoutException

class ManagerViewModel (
    private val repository: ManagerRepository
    ) : ViewModel() {


    var authListener: AuthListener<Any>? = null

//    fun getManagerTicketData(userId: Int,serviceId:Int) {
//        if (userId == null) {
//            authListener?.onValidationError("User id cannot be nil")
//            return
//        }
//
//        authListener?.onStarted()
//
//        Coroutines.main {
//            try {
//                val managerTicketResponse = repository.managerTicketCount(userId,serviceId)
//
//                if (!managerTicketResponse?.isNullOrEmpty()!!) {
//                    managerTicketResponse.let {
//
//                        authListener?.onSuccess(it, "manager_ticket_count")
//                        return@main
//
//                    }
//                } else {
//                    val errorResponse = managerTicketResponse
//                    if (!errorResponse?.isNullOrEmpty()!!) {
//                        errorResponse[0]?.ErrorMsg?.let {
//                            authListener?.onFailure(it, "manager_ticket_count", false)
//                        }
//                    }
//                }
//
//            } catch (e: ApiException) {
//                authListener?.onFailure(e.message!!, "manager_ticket_count", true)
//            } catch (e: NoInternetException) {
//                authListener?.onFailure(e.message!!, "manager_ticket_count", true)
//            } catch (e: SocketTimeoutException) {
//                authListener?.onFailure(e.message!!, "manager_ticket_count", true)
//            }
//        }
//    }
}
