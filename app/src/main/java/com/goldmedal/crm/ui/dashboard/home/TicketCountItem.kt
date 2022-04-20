package com.goldmedal.crm.ui.dashboard.home

import android.content.Context
import android.util.Log
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.goldmedal.crm.databinding.TicketCountItemBinding
import com.goldmedal.crm.ui.ticket.ServiceTicketActivity
import com.xwray.groupie.viewbinding.BindableItem

private const val TAG = "TicketCountItem"

class TicketCountItem(private val ticketCnt: GetTicketsCountData?, private val context: Context) : BindableItem<TicketCountItemBinding>() {

    override fun bind(viewBinding: TicketCountItemBinding, position: Int) {

        viewBinding.apply {


            txtTicketsCount.text = ticketCnt?.TicketCount.toString()
//            txtTicketName.text = context.getString(R.string.str_assigned_tickets)
            txtTicketName.text = ticketCnt?.TicketName

            when (position) {

                0 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialGreen, null))
                        imvTickets.setImageResource(R.drawable.accepted_ticket)
                    }
                }
                1 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialIndigo, null))
                        imvTickets.setImageResource(R.drawable.visited_ticket)
                    }
                }

                2 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialPink, null))
                        imvTickets.setImageResource(R.drawable.rescheduled_ticket)
                    }
                }
                3 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialLime, null))
                        imvTickets.setImageResource(R.drawable.closed_ticket)
                    }
                }


                4 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialAmber, null))
                        imvTickets.setImageResource(R.drawable.reassign_ticket)
                    }
                }
                5 -> {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        rootBackground.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialBlue, null))
                        imvTickets.setImageResource(R.drawable.open_ticket)
                    }
                }
                else -> {

                }


            }

            rootBackground.setOnClickListener {

                Log.d(TAG, "bind: clicked: " + ticketCnt + "position : " + position)
                ServiceTicketActivity.start(context, ticketCnt)
            }

        }
    }

    override fun getLayout() = R.layout.ticket_count_item


    override fun initializeViewBinding(view: View): TicketCountItemBinding = TicketCountItemBinding.bind(view)


}  //, private val callBackListener: IStatusListener?
