package com.goldmedal.crm.ui.parts

import android.content.Context
import android.util.Log
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.UsedItemAndPartData
import com.goldmedal.crm.databinding.UsedItemRowBinding
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.xwray.groupie.viewbinding.BindableItem

class UsedItemAndPartRow(private val itemAndPartData: UsedItemAndPartData?, private val context: Context?, private val callBackListener: UsedPartListener) : BindableItem<UsedItemRowBinding>() {

    override fun bind(viewBinding: UsedItemRowBinding, position: Int) {

        viewBinding.apply {

            textItemName.text = itemAndPartData?.ItemName

            textViewParts.setOnClickListener {
                callBackListener?.itemClicked(itemAndPartData?.ItemSlno,itemAndPartData?.ItemName,"ItemAndPart")
            }
        }
    }

    override fun getLayout() = R.layout.used_item_row




    override fun initializeViewBinding(view: View) = UsedItemRowBinding.bind(view)


}