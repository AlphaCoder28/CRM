package com.goldmedal.crm.util

import android.R
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

fun dpToPx(context: Context, dpVal: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal,
        context.resources.displayMetrics
    )
        .toInt()
}



fun xhdpi(mcontext: Context, value: Int): Int {
    val metrics = mcontext.resources.displayMetrics
    val densityDpi = (metrics.density * 160f).toInt()
    if (densityDpi < 121) {
        return (value * 0.75).toInt()
    }
    return if (densityDpi < 161) {
        value
    } else if (densityDpi < 241) {
        (1.5 * value).toInt()
    } else if (densityDpi < 321) {
        2 * value
    } else if (densityDpi < 481) {
        3 * value
    } else if (densityDpi < 641) {
        4 * value
    } else {
        value
    }
}

fun rupeeFormat(value: String?): String {
    var strRupee = ""
    // Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
    strRupee = if (value.isNullOrEmpty()) {
        "-"
    } else {
        val f1: Format = NumberFormat.getIntegerInstance(Locale("en", "in"))
        println(f1.format(BigDecimal(value)))
        "\u20B9 " + f1.format(BigDecimal(value))
    }
    return strRupee
}