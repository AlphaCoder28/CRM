package com.goldmedal.hrapp.ui.dialogs

import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.data.repositories.TicketRepository
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException
import com.goldmedal.crm.util.getAddressFromLatLong


import java.net.SocketTimeoutException


class TicketUnacceptanceViewModel(private val repository: TicketRepository) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null
    var ticketNo: Int? = null
    var reasonId: Int? = -1
    var strPincode: String? = ""
    var strRemark: String? = ""
    var strMobileNo: String? = ""
    var stateId: Int? = 0
    var districtId: Int? = 0
    var strCity: String? = ""
    var strAddressLine1: String? = ""
    var strAddressLine2: String? = ""
    var strAddressLine3: String? = ""
    var strDeviceId: String? = null
    var userId: Int? = null
    var ticketId: Int? = null
    var type: Int = 1
    var strLatitude: String? = null
    var strLongitude: String? = null
    var strLocation: String? = null

    fun getLoggedInUser() = repository.getLoggedInUser()

    fun getPincodeWiseStateDistrict() {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "info_pincode")
            return
        } else if (strPincode.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Enter Pincode", "info_pincode")
            return
        }

        apiListener?.onStarted("info_pincode")

        Coroutines.main {
            try {
                val pincodeResponse = repository.getPincodeWiseStateDistrict(userId!!, strPincode!!)

                if (!pincodeResponse.geographicalData?.isNullOrEmpty()!!) {
                    pincodeResponse.geographicalData.let {
                        apiListener?.onSuccess(it, "info_pincode")

                        return@main
                    }
                } else {
                    val errorResponse = pincodeResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {

                            apiListener?.onError(it, "info_pincode", false)
                        }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "info_pincode", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "info_pincode", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "info_pincode", true)
            }
        }

    }


    fun onRejectTicketButtonClick(view: View) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "tkt_unacceptance_decline")
            return
        } else if (ticketNo == -1) {
            apiListener?.onValidationError("Ticket id cannot be nil", "tkt_unacceptance_decline")
            return
        }

//       else  if (reasonId == null) {
//            apiListener?.onValidationError("reason id cannot be nil", "tkt_unacceptance_decline")
//            return
//        }
        else if (reasonId == -1) {
            apiListener?.onValidationError(
                "Please Select Ticket Unacceptance Reason",
                "tkt_unacceptance_decline"
            )
            return
        }

//           else if (strRemark.isNullOrEmpty()) {
//            apiListener?.onValidationError("Please Enter Remark", "tkt_unacceptance_decline")
//            return
//        }

        if (reasonId == 2) {
            when {
                strPincode.isNullOrEmpty() -> {
                    apiListener?.onValidationError(
                        "Please Enter Pincode",
                        "tkt_unacceptance_decline"
                    )
                    return
                }
                stateId == 0 -> {
                    apiListener?.onValidationError(
                        "Please select State",
                        "tkt_unacceptance_decline"
                    )
                    return
                }
                districtId == 0 -> {
                    apiListener?.onValidationError(
                        "Please select District",
                        "tkt_unacceptance_decline"
                    )
                    return
                }
                strCity.isNullOrEmpty() -> {
                    apiListener?.onValidationError("Please Enter City", "tkt_unacceptance_decline")
                    return
                }
                strAddressLine1.isNullOrEmpty() -> {
                    apiListener?.onValidationError(
                        "Please Enter Address 1",
                        "tkt_unacceptance_decline"
                    )
                    return
                }
            }
        } else {
            strPincode = "0"
            strCity = "-"
            strAddressLine1 = "-"
            strAddressLine2 = "-"
            strAddressLine3 = "-"

        }

        apiListener?.onStarted("tkt_unacceptance_decline")

        if (strRemark.isNullOrEmpty()) {
            strRemark = "-"
        }

        if (strAddressLine2.isNullOrEmpty()) {
            strAddressLine2 = "-"
        }

        if (strAddressLine3.isNullOrEmpty()) {
            strAddressLine3 = "-"
        }



        Coroutines.main {
            try {
                val rejectResponse = repository.rejectTicket(
                    userId!!,
                    ticketNo!!,
                    reasonId!!,
                    strPincode!!,
                    stateId!!,
                    districtId!!,
                    strCity!!,
                    strAddressLine1!!,
                    strAddressLine2!!,
                    strAddressLine3!!,
                    strRemark!!,
                    type,
                    strLatitude!!,
                    strLongitude!!,
                    strLocation!!
                )

                if (rejectResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!rejectResponse?.acceptRejectTkt?.isNullOrEmpty()!!) {
                        rejectResponse.acceptRejectTkt.let {
                            apiListener?.onSuccess(it, "tkt_unacceptance_decline")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = rejectResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(
                                it,
                                "tkt_unacceptance_decline",
                                false
                            )
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "tkt_unacceptance_decline", true)
            }
        }

    }

}