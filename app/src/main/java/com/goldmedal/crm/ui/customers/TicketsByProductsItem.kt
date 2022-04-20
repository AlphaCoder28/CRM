package com.goldmedal.crm.ui.customers

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.common.TimelineSegment
import com.goldmedal.crm.data.model.TicketsByProductsData
import com.goldmedal.crm.databinding.TicketsByProductsItemBinding
import com.goldmedal.crm.util.rupeeFormat
import com.xwray.groupie.viewbinding.BindableItem

class TicketsByProductsItem(private val data: TicketsByProductsData?, private val context: Context) : BindableItem<TicketsByProductsItemBinding>() {//, private val callBackListener: IStatusListener?


    override fun bind(viewBinding: TicketsByProductsItemBinding, position: Int) {

        viewBinding.apply {

            textViewQrCodeNo.text = data?.ProductQRCode
            textViewTicketStatus.text = data?.TicketStatus
            textViewProductName.text = data?.ProductName
            textViewTicketNo.text = data?.TicketNo



            /*TimeLine*/
                //Assigned Date

//if(layoutTimeline.childCount > 0){
//    layoutTimeline.removeAllViews()
//}


            if (!data?.TicketDate.isNullOrEmpty()) {

                val timelineSegment = TimelineSegment(context, null)
                timelineSegment.setUpperText(data?.TicketDate)
                timelineSegment.setBottomText(context.getString(R.string.str_assigned))
                layoutTimeline.addView(timelineSegment)
            }
                //Accepted Date
//            if (!data?.AcceptDate.isNullOrEmpty()) {
//                val timelineSegment = TimelineSegment(context, null)
//                timelineSegment.setUpperText(data?.AcceptDate)
//                timelineSegment.setBottomText(context.getString(R.string.str_accepted))
//                layoutTimeline.addView(timelineSegment)
//            }

            //Appointment Date
            if (!data?.AppointmentDate.isNullOrEmpty()) {
                val timelineSegment = TimelineSegment(context, null)
                timelineSegment.setUpperText(data?.AppointmentDate)
                timelineSegment.setBottomText(context.getString(R.string.str_appointment))
                layoutTimeline.addView(timelineSegment)
            }

            //Service Date
            if (!data?.ServiceDate.isNullOrEmpty()) {

                val timelineSegment = TimelineSegment(context, null)
                timelineSegment.setUpperText(data?.ServiceDate)
                timelineSegment.setBottomText(context.getString(R.string.str_serviced))
                layoutTimeline.addView(timelineSegment)
            }

            textViewDivision.text = data?.DivisionName
            textViewCategory.text = data?.CategoryName

            textViewProductIssues.text = data?.ProductIssues
            textViewProductSymptoms.text = data?.Symptoms

            textViewWarrantyValidity.text = data?.WarrantyUptoDate
            textViewServiceFee.text = rupeeFormat(data?.TicketTotalCost.toString())


            if (!data?.EnginnerRemarks.isNullOrEmpty()){
                textViewEngineerRemarks.visibility = View.VISIBLE
                textViewEngineerRemarks.text = context.getString(R.string.str_remark) + " : "+ data?.EnginnerRemarks
            }else{
                textViewEngineerRemarks.text = ""
                textViewEngineerRemarks.visibility = View.GONE
            }




//            layoutRoot.setOnClickListener {
//                callBackListener?.observeCheckInStatus(data?.TicketID)
//            }

        }
    }
    override fun getLayout() = R.layout.tickets_by_products_item
    override fun initializeViewBinding(view: View) = TicketsByProductsItemBinding.bind(view)
}