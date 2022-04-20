//package com.goldmedal.hrapp.ui.dialogs
//
//import android.view.View
//import androidx.lifecycle.ViewModel
//import com.goldmedal.crm.common.ApiStageListener
//import com.goldmedal.crm.data.network.GlobalConstant
//import com.goldmedal.crm.data.repositories.TicketRepository
//import com.goldmedal.crm.util.ApiException
//import com.goldmedal.crm.util.Coroutines
//import com.goldmedal.crm.util.NoInternetException
//import com.goldmedal.crm.util.getAddressFromLatLong
//import retrofit2.http.Field
//
//
//import java.net.SocketTimeoutException
//
//
//class TicketOTPViewModel(private val repository: TicketRepository) : ViewModel() {
//
//    var apiListener: ApiStageListener<Any>? = null
//    var strTicketNo: String? = ""
//    var strMobileNo: String? = ""
//    var intTicketId: Int? = 0
//    var intUserId: Int? = 0
//    var strCustName: String? = ""
//
//    fun getLoggedInUser() = repository.getLoggedInUser()
//
//
//
//    fun onRejectTicketButtonClick(view: View) {
//
//        if (userId == null) {
//            apiListener?.onValidationError("User id cannot be nil", "tkt_unacceptance_decline")
//            return
//        } else if (ticketNo == -1) {
//            apiListener?.onValidationError("Ticket id cannot be nil", "tkt_unacceptance_decline")
//            return
//        }
//
////       else  if (reasonId == null) {
////            apiListener?.onValidationError("reason id cannot be nil", "tkt_unacceptance_decline")
////            return
////        }
//        else if (reasonId == -1) {
//            apiListener?.onValidationError(
//                "Please Select Ticket Unacceptance Reason",
//                "tkt_unacceptance_decline"
//            )
//            return
//        }
//
////           else if (strRemark.isNullOrEmpty()) {
////            apiListener?.onValidationError("Please Enter Remark", "tkt_unacceptance_decline")
////            return
////        }
//
//        if (reasonId == 2) {
//            when {
//                strPincode.isNullOrEmpty() -> {
//                    apiListener?.onValidationError(
//                        "Please Enter Pincode",
//                        "tkt_unacceptance_decline"
//                    )
//                    return
//                }
//                stateId == 0 -> {
//                    apiListener?.onValidationError(
//                        "Please select State",
//                        "tkt_unacceptance_decline"
//                    )
//                    return
//                }
//                districtId == 0 -> {
//                    apiListener?.onValidationError(
//                        "Please select District",
//                        "tkt_unacceptance_decline"
//                    )
//                    return
//                }
//                strCity.isNullOrEmpty() -> {
//                    apiListener?.onValidationError("Please Enter City", "tkt_unacceptance_decline")
//                    return
//                }
//                strAddressLine.isNullOrEmpty() -> {
//                    apiListener?.onValidationError(
//                        "Please Enter Address",
//                        "tkt_unacceptance_decline"
//                    )
//                    return
//                }
//            }
//        } else {
//            strPincode = "0"
//            strCity = "-"
//            strAddressLine = "-"
//
//        }
//
//        apiListener?.onStarted("tkt_unacceptance_decline")
//
//        if (strRemark.isNullOrEmpty()) {
//            strRemark = "-"
//        }
//
//        Coroutines.main {
//            try {
//                val rejectResponse = repository.rejectTicket(
//                    userId!!,
//                    ticketNo!!,
//                    reasonId!!,
//                    strPincode!!,
//                    stateId!!,
//                    districtId!!,
//                    strCity!!,
//                    strAddressLine!!,
//                    strRemark!!,
//                    type,
//                    strLatitude!!,
//                    strLongitude!!,
//                    strLocation!!
//                )
//
//                if (rejectResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
//                    if (!rejectResponse?.acceptRejectTkt?.isNullOrEmpty()!!) {
//                        rejectResponse.acceptRejectTkt.let {
//                            apiListener?.onSuccess(it, "tkt_unacceptance_decline")
//                            return@main
//                        }
//                    }
//                } else {
//                    val errorResponse = rejectResponse.Errors
//                    if (!errorResponse?.isNullOrEmpty()!!) {
//                        errorResponse[0]?.ErrorMsg?.let {
//                            apiListener?.onError(
//                                it,
//                                "tkt_unacceptance_decline",
//                                false
//                            )
//                        }
//                    }
//                }
//
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
//            } catch (e: NoInternetException) {
//                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
//            } catch (e: SocketTimeoutException) {
//                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
//            }
//        }
//
//    }
//
//
//}