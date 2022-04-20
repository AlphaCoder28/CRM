package com.goldmedal.crm.common

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.*


// create an OnDateSetListener
var dateSetListener: DatePickerDialog.OnDateSetListener? = null
var calendar: Calendar = Calendar.getInstance()
fun initDateListener() {

    dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {


//            cal.set(Calendar.YEAR, year)
//            cal.set(Calendar.MONTH, monthOfYear)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //updateDateInView()
        }
    }
}

fun Context.showDatePicker() {

    DatePickerDialog(this,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
}

