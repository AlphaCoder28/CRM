package com.goldmedal.crm.ui.ticket


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetAcceptedTicketData
import com.goldmedal.crm.databinding.AcceptedTicketRowBinding
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*


class AcceptedTicketItem(private val acceptedTicketData: GetAcceptedTicketData?, private val context: Context, private val callBackListener: IStatusListener?, statusby: Int?) : BindableItem<AcceptedTicketRowBinding>() { //Item()

    var statusBy = statusby

    override fun getLayout() = R.layout.accepted_ticket_row

    @SuppressLint("ResourceAsColor")
    override fun bind(viewBinding: AcceptedTicketRowBinding, position: Int) {
        viewBinding.apply {
            txtTicketNo.text = acceptedTicketData?.Tktno
            txtTimeSlot.text = acceptedTicketData?.AppointmentDate + " | " + acceptedTicketData?.TimeSlot
            txtCustName.text = acceptedTicketData?.CustName
            txtAddress.text = acceptedTicketData?.CustAddress
            txtProductIssue.text = acceptedTicketData?.ProductIssues

            if(!acceptedTicketData?.ReScheduleDate.isNullOrEmpty()){
                txtRescheduleDate.text = "Rescheduled On : " + acceptedTicketData?.ReScheduleDate+ " | " + acceptedTicketData?.TimeSlot
            }else{
                txtRescheduleDate.text = "-"
            }


            //acceptedTicketData?.TktStatus.equals("Reschedule")
//            if (statusBy == 1) {
//                layoutViewReassign.isVisible = true
//                layoutClose.isVisible = true
//            } else {
//                layoutViewReassign.isVisible = false
//                layoutClose.isVisible = false
//            }

            if (listOf("high", "urgent").contains(acceptedTicketData?.TktPriority?.toLowerCase(Locale.getDefault()))) {
//            if (acceptedTicketData?.TktPriority?.equals("high", ignoreCase = true) == true) {
                textViewPriority.visibility = View.VISIBLE
                textViewPriority.text = context.getString(R.string.str_urgent)
            } else {
                textViewPriority.visibility = View.GONE
            }

            // - - - Pending Ticket(yellow), Visited(Indigo), Assigned, Reschedule(Pink),  Not Accepted(open)(Blue), Urgent Ticket(Red), InProgress Ticket(Teal), Closed(Lime), Reassign(Amber)
            val tktStatus= acceptedTicketData?.TktStatus

            when (tktStatus?.toLowerCase()) {

                "pending ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorYellow,null))
                    }
                }
                "visited" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorMaterialIndigo,null))
                    }
                }
                "reschedule" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorMaterialPink,null))
                    }
                }
                "not accepted" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorBlue,null))
                    }
                }
                "urgent ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorRed,null))
                    }
                }
                "inprogress ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.material_teal_700,null))
                    }
                }
                "closed" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorMaterialLime,null))
                    }
                }
                "reassign" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorMaterialAmber,null))
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewStatus.setTextColor(context.resources.getColor(R.color.colorMaterialGreen,null))
                    }
                }
            }
            textViewStatus.text = tktStatus

            layoutCall.setOnClickListener {
                Log.d("TAG", "bind: only call")
                if (!acceptedTicketData?.CustContactNo.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = (Uri.parse("tel:" + acceptedTicketData?.CustContactNo))
                    context.startActivity(intent)
                }
            }

            textViewDetails.setOnClickListener {
                TicketViewDetailsActivity.start(context, acceptedTicketData?.TicketID
                        ?: -1, acceptedTicketData?.Tktno)
            }


            textViewClose.setOnClickListener { v: View? ->
                callBackListener?.cancelTicket(acceptedTicketData, 6)
            }


            textViewReassign.setOnClickListener { v: View? ->
                callBackListener?.cancelTicket(acceptedTicketData, 5)
            }

            txtTicketReschedule.setOnClickListener { v: View? ->
                callBackListener?.cancelTicket(acceptedTicketData, 4)
            }

            if(statusBy == 4 || statusBy == 5 || statusBy == 6){
                btnMain.visibility = View.GONE
            }else{
                btnMain.visibility = View.VISIBLE

                layoutRoot.setOnClickListener {
                    callBackListener?.observeCheckInStatus(acceptedTicketData?.TicketID)
                }
            }




//            if (statusBy == 5 || statusBy == 6) {
//                return
//            } else {
//
////                layoutCancel.setOnClickListener {
////                    callBackListener?.cancelTicket(acceptedTicketData,3)
////                }
//            }

        }
    }

    override fun initializeViewBinding(view: View): AcceptedTicketRowBinding = AcceptedTicketRowBinding.bind(view)


}