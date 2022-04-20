package com.goldmedal.crm.util

interface DateListener {
    fun onDateSelected(year:Int,month:Int,day:Int,callFrom:String)
}