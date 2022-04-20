package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.UsedItemAndPartData
import com.goldmedal.crm.data.model.UsedPartAndItemData
import com.goldmedal.crm.databinding.UsedPartRowDialogBinding
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.xwray.groupie.viewbinding.BindableItem

class UsedPartAndItemDialogRow(private val partAndItemData: UsedPartAndItemData?, private val context: Context?) : BindableItem<UsedPartRowDialogBinding>() {

    override fun bind(viewBinding: UsedPartRowDialogBinding, position: Int) {

        viewBinding.apply {
            textPartName.text = partAndItemData?.Item
        }
    }

    override fun getLayout() = R.layout.used_part_row_dialog




    override fun initializeViewBinding(view: View) = UsedPartRowDialogBinding.bind(view)


}