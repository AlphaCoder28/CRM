package com.goldmedal.crm.ui.customers

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.ContactsData

import com.goldmedal.crm.databinding.ContactsItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class ContactsItem(private val contacts: ContactsData?, private val context: Context) : BindableItem<ContactsItemBinding>() {

    override fun bind(viewBinding: ContactsItemBinding, position: Int) {

        viewBinding.apply {


//            txtProductName.isSelected = true
            txtCustomerName.text = contacts?.CustName
            txtCustomerAddress.text = contacts?.CustAddress



            if (!contacts?.CustName.isNullOrEmpty()) {
                txtCustomerName.paintFlags = txtCustomerName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                txtCustomerName.setTextColor(context.resources.getColor(R.color.colorHalfDay))

            } else {
                txtCustomerName.paintFlags = 0
                txtCustomerName.setTextColor(context.resources.getColor(R.color.colorBlack))
            }






            imvCall.setOnClickListener {

                if (!contacts?.CustContactNo.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = (Uri.parse("tel:" + contacts?.CustContactNo))
                    context.startActivity(intent)
                }
            }


            rootLayout.setOnClickListener {


                CustomerProductsActivity.start(context,contacts?.CustName,contacts?.CustomerID)


            }
        }
    }

    override fun getLayout() = R.layout.contacts_item


    override fun initializeViewBinding(view: View) = ContactsItemBinding.bind(view)


}