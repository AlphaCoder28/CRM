package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.UsedPartAndItemData
import com.goldmedal.crm.databinding.UsedPartRowBinding
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.xwray.groupie.viewbinding.BindableItem

class UsedPartAndItemRow(private val partAndItemData: UsedPartAndItemData?, private val context: Context, private val callBackListener: UsedPartListener) : BindableItem<UsedPartRowBinding>() {

    override fun bind(viewBinding: UsedPartRowBinding, position: Int) {

        viewBinding.apply {

            textPartName.text = partAndItemData?.PartName

            textViewItems.setOnClickListener {
                callBackListener?.itemClicked(partAndItemData?.PartSlno,partAndItemData?.PartName,"PartAndItem")
            }
        }
    }

    override fun getLayout() = R.layout.used_part_row


    override fun initializeViewBinding(view: View) = UsedPartRowBinding.bind(view)


}