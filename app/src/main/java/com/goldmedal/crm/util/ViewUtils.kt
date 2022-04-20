
package com.goldmedal.crm.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.goldmedal.crm.R
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun Context.alertDialog(message: String) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)

    builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, id12: Int) {

        }
    })
//    builder.setNegativeButton(R.string.str_cancel, object : DialogInterface.OnClickListener {
//        override fun onClick(dialog: DialogInterface, id13: Int) {}
//    })


    val alertDialog = builder.create()
    alertDialog.show()
}


//Close/Hide virtual keyboard


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}



fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun generateRandomCaptcha(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var passWord = ""
    for (i in 0..5) {
        passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
    }
    return passWord
}

