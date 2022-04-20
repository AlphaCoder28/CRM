package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetTicketDetailsForEngineerData
import com.goldmedal.crm.databinding.OtherTicketRowBinding
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.interfaces.OnServiceTicketClickListener
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*

class ServiceTicketItem(
    private val serviceTicketData: GetTicketDetailsForEngineerData?,
    private val context: Context,
    private val callBackListener: OnServiceTicketClickListener?,
    private var callBackStatusListener: IStatusListener? = null,
    private val statusBy: Int
) : BindableItem<OtherTicketRowBinding>()  { //Item()


    override fun getLayout() = R.layout.other_ticket_row
    override fun bind(viewBinding: OtherTicketRowBinding, position: Int) {
        viewBinding.apply {

            txtTicketNo.text = serviceTicketData?.Tktno
            txtTimeSlot.text = serviceTicketData?.AppointmentDate + " | "+ serviceTicketData?.TimeSlot
            txtCustName.text = serviceTicketData?.CustName
            txtAddress.text = serviceTicketData?.CustAddress
            txtProductIssue.text = serviceTicketData?.ProductIssues


            if (listOf("high", "urgent").contains(serviceTicketData?.TktPriority?.toLowerCase(Locale.getDefault()))) {
//            if (serviceTicketData?.TktPriority?.equals("high", ignoreCase = true) == true) {
                textViewPriority.visibility = View.VISIBLE
                textViewPriority.text = context.getString(R.string.str_urgent)
            }else{
                textViewPriority.visibility = View.GONE
            }

            if(!serviceTicketData?.ReScheduleDate.isNullOrEmpty()){
                txtRescheduleDate.text = "Rescheduled On : " + serviceTicketData?.ReScheduleDate+ " | " + serviceTicketData?.TimeSlot
            }else{
                txtRescheduleDate.text = "-"
            }


//            if (serviceTicketData?.TktStatus?.toLowerCase(Locale.getDefault()).equals("accepted")) {
//                textViewCancel.visibility = View.VISIBLE
//            }else{
//                textViewCancel.visibility = View.GONE
//            }

            val tktStatus= serviceTicketData?.TktStatus

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
                if (!serviceTicketData?.CustContactNo.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = (Uri.parse("tel:" + serviceTicketData?.CustContactNo))
                    context.startActivity(intent)
                }
            }


            layoutViewDetails.setOnClickListener {
                TicketViewDetailsActivity.start(context, serviceTicketData?.TicketID ?: -1,serviceTicketData?.Tktno)
            }

//            layoutCancel.setOnClickListener {
//                callBackListener?.onTicketClick(serviceTicketData,3)
//            }

//            layoutClose.setOnClickListener {
//                callBackListener?.onTicketClick(serviceTicketData,6)
//            }

//            layoutViewReassign.setOnClickListener {
//                callBackListener?.onTicketClick(serviceTicketData,5)
//            }
          //  layoutRoot.setOnClickListener {

//                when (statusBy) {
//
//                    //2 - Accepted Tickets
//                    //3 - Visited Tickets
//                    //4 - Rescheduled Tickets
//                    2, 3, 4 ->{
//
//                        callBackListener?.onTicketClick(serviceTicketData)
//                    }
//
//                }


            layoutRoot.setOnClickListener {
                if (tktStatus?.toLowerCase().equals("closed") || tktStatus?.toLowerCase()
                        .equals("reassign")
                ) {
                    Log.d("statusBy  - - - - ", tktStatus?.toLowerCase())
                } else {
                    Log.d("statusBy  - - - - ", tktStatus?.toLowerCase())
                    callBackStatusListener?.observeCheckInStatus(serviceTicketData?.TicketID)
                }

            }

        }
    }

    override fun initializeViewBinding(view: View): OtherTicketRowBinding = OtherTicketRowBinding.bind(view)


}