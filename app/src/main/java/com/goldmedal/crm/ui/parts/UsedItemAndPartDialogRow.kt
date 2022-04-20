package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.UsedItemAndPartData
import com.goldmedal.crm.databinding.UsedItemRowDialogBinding
import com.xwray.groupie.viewbinding.BindableItem

class UsedItemAndPartDialogRow(private val itemAndPartData: UsedItemAndPartData?, private val context: Context?) : BindableItem<UsedItemRowDialogBinding>() {

    override fun bind(viewBinding: UsedItemRowDialogBinding, position: Int) {

        viewBinding.apply {
            textItemName.text = itemAndPartData?.Part
        }
    }

    override fun getLayout() = R.layout.used_item_row_dialog




    override fun initializeViewBinding(view: View) = UsedItemRowDialogBinding.bind(view)


}