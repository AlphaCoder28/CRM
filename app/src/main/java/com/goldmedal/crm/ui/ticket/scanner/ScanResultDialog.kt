package com.goldmedal.crm.ui.ticket.scanner
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.util.TypedValue
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.R
//import androidx.appcompat.app.AppCompatDialog
//import com.budiyev.android.libdemoapp.R
//import com.google.zxing.Result
//
//
//class ScanResultDialog(
//    context: Context,
//    result: Result
//) :
//    AppCompatDialog(context, resolveDialogTheme(context)) {
//    companion object {
//        private fun resolveDialogTheme(context: Context): Int {
//            val outValue = TypedValue()
//            context.theme
//                .resolveAttribute(R.attr.alertDialogTheme, outValue, true)
//            return outValue.resourceId
//        }
//    }
//
//    init {
//        setTitle(R.string.scan_result)
//        setContentView(R.layout.dialog_scan_result)
//        (findViewById<View>(R.id.result) as TextView?)!!.text = result.text
//        (findViewById<View>(R.id.format) as TextView?)!!.text = result.barcodeFormat.toString()
//        findViewById<View>(R.id.copy)!!.setOnClickListener { v: View? ->
//            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
//                .setPrimaryClip(ClipData.newPlainText(null, result.text))
//            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show()
//            dismiss()
//        }
//        findViewById<View>(R.id.close)!!.setOnClickListener { v: View? -> dismiss() }
//    }
//}