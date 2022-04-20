package com.goldmedal.crm.ui.invoice

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.AddedInvoiceItemData
import com.goldmedal.crm.data.model.GetItemForInvoiceData
import com.goldmedal.crm.databinding.InvoiceAddedItemRowBinding
import com.goldmedal.crm.util.interfaces.OnRemoveInvoiceItemListener
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.xwray.groupie.viewbinding.BindableItem

class AddedInvoiceItem(
    private val addedInvoiceItemData: AddedInvoiceItemData?,
    private val callBackListener: OnRemoveInvoiceItemListener,
    private val context: Context
) : BindableItem<InvoiceAddedItemRowBinding>() {

    override fun bind(viewBinding: InvoiceAddedItemRowBinding, position: Int) {

        viewBinding.apply {

            txtInvItemName.text = addedInvoiceItemData?.itemName.toString()
            txtInvItemPrice.text = addedInvoiceItemData?.PricePerUnit.toString()
            txtInvItemQty.text = addedInvoiceItemData?.Quantity.toString()
            txtInvPreTaxAmnt.text = addedInvoiceItemData?.PreTaxAmount.toString()
            txtInvTaxAmount1.text = addedInvoiceItemData?.TaxAmount1.toString()
            txtInvTaxAmount2.text = addedInvoiceItemData?.TaxAmount2.toString()
            txtInvTotalAmnt.text = addedInvoiceItemData?.FinalPrice.toString()
            txtInvTotalTax.text = ((addedInvoiceItemData?.TaxAmount1 ?: 0) + (addedInvoiceItemData?.TaxAmount2  ?: 0)).toString()

            tvDelete.setOnClickListener {
                callBackListener.onRemoveClick(addedInvoiceItemData?.Slno ?: -1,position)
            }
        }
    }

    override fun getLayout() = R.layout.invoice_added_item_row


    override fun initializeViewBinding(view: View) = InvoiceAddedItemRowBinding.bind(view)


}