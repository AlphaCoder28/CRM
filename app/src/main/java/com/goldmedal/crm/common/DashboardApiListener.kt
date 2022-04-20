package com.goldmedal.crm.common

interface DashboardApiListener<T> {
    fun onStarted(callFrom:String)
    fun onSuccess(_object: List<T?>,callFrom:String,timestamp: String)
    fun onError(message: String, callFrom: String, isNetworkError: Boolean)
    fun onValidationError(message: String, callFrom: String)

}