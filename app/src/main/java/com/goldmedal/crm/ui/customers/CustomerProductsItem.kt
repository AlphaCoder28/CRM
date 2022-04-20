package com.goldmedal.crm.ui.customers

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.CustomerProductsData
import com.goldmedal.crm.databinding.CustomerProductsItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class CustomerProductsItem(
    private val data: CustomerProductsData?,
    private val context: Context,
    private val customerId: Int?,
    private val customerName: String?
) : BindableItem<CustomerProductsItemBinding>() {//, private val callBackListener: IStatusListener?


    override fun bind(viewBinding: CustomerProductsItemBinding, position: Int) {

        viewBinding.apply {

//            textViewCategoryName.isSelected = true
//            txtProductName.isSelected = true
//            txtTicketNo.isSelected = true
            textViewProduct.text = data?.ProductName

            if (!data?.ProductColor.isNullOrEmpty()) {
                textViewProduct.append(" (${data?.ProductColor})")
            }
            textViewCategoryName.text = data?.CategoryName


            Glide.with(context)
                .load(data?.ProductImg)
                .fitCenter()
                .placeholder(R.drawable.product_black)
                .into(iconProfile)

            rootLayout.setOnClickListener {


                TicketsByProductsActivity.start(context,customerName = customerName,customerId = customerId, productId = data?.ProductID)

//                if (ticketAppointment?.isTicketAccepted == true) {
//                    callBackListener?.observeCheckInStatus(ticketAppointment.TicketID)
//                } else {
//                    context.toast("Please accept ticket no ${ticketAppointment?.Tktno} before visit")
//                }
            }

        }
    }
    override fun getLayout() = R.layout.customer_products_item
    override fun initializeViewBinding(view: View) = CustomerProductsItemBinding.bind(view)
}