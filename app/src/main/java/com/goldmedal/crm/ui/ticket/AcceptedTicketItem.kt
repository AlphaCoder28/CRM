package com.goldmedal.crm.ui.ticket


/*
// Logic in the code:

1. **Data Binding**:
- Binds ticket data (like ticket number, customer name, appointment details) to the UI elements.

2. **Reschedule Information**:
- Displays reschedule date and time if available, otherwise shows a placeholder.

3. **Priority Highlight**:
- Highlights tickets with "high" or "urgent" priority.

4. **Status Handling**:
- Displays ticket status with color-coded text based on the status (e.g., "Pending", "Visited", "Reschedule").
- Colors are mapped using `when` conditions.

5. **Call Functionality**:
- Allows users to make a call to the customer by clicking the call button.

6. **Ticket Details**:
- Navigates to the ticket details page when the "Details" button is clicked.

7. **Ticket Actions**:
- Buttons for ticket actions like "Close", "Reassign", and "Reschedule" trigger callback functions with specific status codes.

8. **Check-in Status**:
- Observes and handles ticket check-in status when the root layout is clicked (if the status is not 4, 5, or 6).

9. **Visibility Control**:
- Shows or hides action buttons (e.g., main button) based on the current status of the ticket.
*/


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

//This code binds ticket data to UI elements, displays reschedule info if available, sets priority and status colors, and handles various button clicks for actions like calling, viewing details, canceling, reassigning, and tracking check-in status.
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


//This function is called with the ticket data and a status code to handle actions like closing, reassigning, or rescheduling the ticket.
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
//                This function is called to  check-in status of a ticket when layoutRoot is clicked for particular conditions.
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