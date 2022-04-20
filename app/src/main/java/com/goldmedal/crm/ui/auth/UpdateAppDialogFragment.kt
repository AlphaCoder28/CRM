package com.goldmedal.hrapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.goldmedal.crm.R

class UpdateAppDialogFragment : DialogFragment() {

private var forceUpdate: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.update_app_dialog, container, false)
isCancelable = false


        val btnUpdate = view.findViewById<Button>(R.id.buttonUpdate)
        val btnCancel = view.findViewById<Button>(R.id.buttonCancel)




arguments?.let {
    forceUpdate = it.getBoolean("forceUpdate")
}

        if(forceUpdate){
            btnCancel.visibility = View.GONE
        }


        btnUpdate.setOnClickListener {
            callBack?.pressedUpdate()
            dismiss()
        }
        btnCancel.setOnClickListener {
            callBack?.pressedNoThanks()
            dismiss()
        }

        return view

    }
    companion object {
        const val TAG = "UpdateAppDialog"
       private var callBack: OnCancelUpdate? = null
        fun newInstance(forceUpdate: Boolean?,listener: OnCancelUpdate): UpdateAppDialogFragment {
            //Attached Interface
            callBack = listener

            val fragment = UpdateAppDialogFragment()
            val args = Bundle()
            args.putBoolean("forceUpdate", forceUpdate ?: false)
            fragment.arguments = args
            return fragment
        }
    }

    interface OnCancelUpdate{
        fun pressedNoThanks()
        fun pressedUpdate()
    }
}