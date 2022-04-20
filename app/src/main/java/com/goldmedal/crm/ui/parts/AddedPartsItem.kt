package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.AddedPartsData
import com.goldmedal.crm.data.model.SelectPartsListData
import com.goldmedal.crm.databinding.PartsAddedItemRowBinding
import com.goldmedal.crm.util.interfaces.OnRemoveInvoiceItemListener
import com.xwray.groupie.viewbinding.BindableItem

class AddedPartsItem(
    private val addedPartsItemData: AddedPartsData?,
    private val callBackListener: OnRemoveInvoiceItemListener,
    private val context: Context
) : BindableItem<PartsAddedItemRowBinding>() {

    override fun bind(viewBinding: PartsAddedItemRowBinding, position: Int) {

        viewBinding.apply {

            txtPartName.text = addedPartsItemData?.PartName
            txtPartQty.text = addedPartsItemData?.QTY.toString()

            tvDelete.setOnClickListener {
                callBackListener.onRemoveClick(addedPartsItemData?.PartID ?: -1,position)
            }
        }
    }

    override fun getLayout() = R.layout.parts_added_item_row


    override fun initializeViewBinding(view: View) = PartsAddedItemRowBinding.bind(view)


}