package com.goldmedal.crm.ui.parts


import androidx.lifecycle.ViewModel
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.data.repositories.HomeRepository
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException
import org.json.JSONArray
import java.net.SocketTimeoutException


class PartsViewModel(
        private val repository: HomeRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getLoggedInUser()

    var apiListener: ApiStageListener<Any>? = null

    fun getItemAndPartDetail(itemSlNo: Int,searchBy: String) {

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("itemAndPartDetail")

        Coroutines.main {
            try {
                val itemAndPartResponse = repository.getItemAndPartDetails(itemSlNo,strSearchBy)

                if (!itemAndPartResponse.itemAndPartData?.isNullOrEmpty()!!) {
                    itemAndPartResponse.itemAndPartData.let {
                        apiListener?.onSuccess(it, "itemAndPartDetail")
                        return@main
                    }
                }else {

                    val errorResponse = itemAndPartResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "itemAndPartDetail", false)
                        }
                    }
                }



            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "itemAndPartDetail", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "itemAndPartDetail", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "itemAndPartDetail", true)


            }
        }

    }


    fun getPartAndItemDetail(partSlNo: Int,searchBy: String) {

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("partAndItemDetail")

        Coroutines.main {
            try {
                val partAndItemResponse = repository.getPartAndItemDetails(partSlNo,strSearchBy)

                if (!partAndItemResponse.partAndItemData?.isNullOrEmpty()!!) {
                    partAndItemResponse.partAndItemData.let {
                        apiListener?.onSuccess(it, "partAndItemDetail")
                        return@main
                    }
                }else {

                    val errorResponse = partAndItemResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "partAndItemDetail", false)
                        }
                    }
                }



            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "partAndItemDetail", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "partAndItemDetail", true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "partAndItemDetail", true)


            }
        }

    }

    // - - - - - - Get list of parts here in stock - - - - -
    fun getStockPartsList(userID: Int,requestNo:String, searchBy:String) {

        var strSearchBy = searchBy

        if (strSearchBy.isEmpty()) {
            strSearchBy = "-"
        }

        apiListener?.onStarted("StockPartsList")

        Coroutines.main {
            try {
                val stockPartsResponse = repository.getStockPartsDetails(userID,requestNo,strSearchBy)

                if (!stockPartsResponse.getSelectPartsList?.isNullOrEmpty()!!) {
                    stockPartsResponse.getSelectPartsList.let {
                        apiListener?.onSuccess(it, "StockPartsList")
                        return@main
                    }
                }else {
                    val errorResponse = stockPartsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "StockPartsList", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "StockPartsList", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message!!, "StockPartsList", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "StockPartsList", true)
            }
        }

    }

    // - - - - - - Get list of parts here in stock - - - - -
    fun getRequestedItemListByUser(requestNo:String) {

        apiListener?.onStarted("RequestedItemListByUser")

        Coroutines.main {
            try {
                val stockPartsResponse = repository.getRequestedPartsByUser(requestNo)

                if (!stockPartsResponse.getSelectPartsList?.isNullOrEmpty()!!) {
                    stockPartsResponse.getSelectPartsList.let {
                        apiListener?.onSuccess(it, "RequestedItemListByUser")
                        return@main
                    }
                }else {
                    val errorResponse = stockPartsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "RequestedItemListByUser", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "RequestedItemListByUser", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message!!, "RequestedItemListByUser", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "RequestedItemListByUser", true)
            }
        }

    }


// - - - - - -  - get parts requirement data here  - - - - - -
    fun getPartsRequirementDetail(userID: Int,custID: Int,tktID: Int) {

    var callFrom = "-"

    if(userID != 0){
        callFrom = "PartsCustomerList"
    }

    if(userID != 0 && custID != 0){
        callFrom = "PartsTicketNoList"
    }

    if(tktID != 0){
        callFrom = "PartsAllDetails"
    }

        apiListener?.onStarted(callFrom)

        Coroutines.main {
            try {
                val partReqResponse = repository.getPartRequirementDetails(userID,custID,tktID)

                if (!partReqResponse.getPartsRequirementData?.isNullOrEmpty()!!) {
                    partReqResponse.getPartsRequirementData.let {
                        apiListener?.onSuccess(it, callFrom)
                        return@main
                    }
                }else {

                    val errorResponse = partReqResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, callFrom, false)
                        }
                    }
                }



            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, callFrom, true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, callFrom, true)


            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, callFrom, true)


            }
        }

    }


    // - - - - - - submit parts requirement here - - - - -
    fun submitPartsRequirement(userID: Int,custID: Int,tktID: Int,partsAddedDetails: JSONArray) {

        apiListener?.onStarted("submit_parts_requirement")

        Coroutines.main {
            try {
                val submitPartsResponse = repository.submitPartsRequirementDetails(userID,tktID,custID,partsAddedDetails)

                if (!submitPartsResponse.submitPartsData?.isNullOrEmpty()!!) {
                    submitPartsResponse.submitPartsData.let {
                        apiListener?.onSuccess(it, "submit_parts_requirement")
                        return@main
                    }
                }else {
                    val errorResponse = submitPartsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "submit_parts_requirement", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "submit_parts_requirement", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message!!, "submit_parts_requirement", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "submit_parts_requirement", true)
            }
        }

    }


    // - - - - - - get list of parts requirement here - - - - -
    fun getPartsRequirementList(userID: Int) {

        apiListener?.onStarted("parts_requirement_list")

        Coroutines.main {
            try {
                val partsReqListResponse = repository.getPartsRequirementList(userID)

                if (!partsReqListResponse.getRequestPartListData?.isNullOrEmpty()!!) {
                    partsReqListResponse.getRequestPartListData.let {
                        apiListener?.onSuccess(it, "parts_requirement_list")
                        return@main
                    }
                }else {
                    val errorResponse = partsReqListResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "parts_requirement_list", false)
                        }
                    }
                }
            }catch (e: ApiException) {
                apiListener?.onError(e.message!!, "parts_requirement_list", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message!!, "parts_requirement_list", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "parts_requirement_list", true)
            }
        }

    }


}
