package com.goldmedal.crm.ui.dashboard.leftpanel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.ContactsData
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.goldmedal.crm.databinding.ContactsItemBinding
import com.goldmedal.crm.databinding.FancyGridItemBinding
import com.goldmedal.crm.ui.customers.CustomerProductsActivity
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.interfaces.OnCardClickListener
import com.xwray.groupie.viewbinding.BindableItem

class FancyItem(private val data: GetTicketsCountData?, private val context: Context, private val callBackListener: OnCardClickListener?) : BindableItem<FancyGridItemBinding>() {




    override fun bind(viewBinding: FancyGridItemBinding, position: Int) {

        viewBinding.apply {
            textViewTicketCount.text = data?.TicketCount.toString()
            textViewTicketName.text = data?.TicketName

            when (position) {
                0 -> {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialBlue, null))
//                        imvTickets.setImageResource(R.drawable.open_ticket)
                    }
                }
                1 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialGreen, null))
                        //imvTickets.setImageResource(R.drawable.accepted_ticket)
                    }
                }
                2 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialIndigo, null))
                        //imvTickets.setImageResource(R.drawable.visited_ticket)
                    }
                }

                3 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialPink, null))
                        //imvTickets.setImageResource(R.drawable.rescheduled_ticket)
                    }
                }
                4 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialLime, null))
                        //imvTickets.setImageResource(R.drawable.closed_ticket)
                    }
                }

                5 -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        itemFancyCardView.setCardBackgroundColor(context.resources.getColor(R.color.colorMaterialAmber, null))
                        //imvTickets.setImageResource(R.drawable.closed_ticket)
                    }
                }
                else -> {

                }


            }

            itemFancyCardView.setOnClickListener {


                callBackListener?.onCardClick(position,data?.TicketId ?: 0)
//                CustomerProductsActivity.start(context,contacts?.CustomerID)


            }
        }
    }

    override fun getLayout() = R.layout.fancy_grid_item


    override fun initializeViewBinding(view: View) = FancyGridItemBinding.bind(view)


}