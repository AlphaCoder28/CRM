package com.goldmedal.crm.ui.auth

import com.goldmedal.crm.data.db.entities.User

//<!--added by shetty 6 jan 21-->
interface AuthListener<T> {
    fun onStarted()
    fun onSuccess(_object: List<T?>,callFrom:String)
 fun onFailure(message: String, callFrom: String, isNetworkError: Boolean)
    fun onValidationError(message: String)
   // fun setCaptcha(strCaptcha: String)
}
