package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.SelectPartsListData
import com.goldmedal.crm.databinding.AvailablePartsRowBinding
import com.xwray.groupie.viewbinding.BindableItem

class AvailablePartsItemRow(private val availablePartsData: SelectPartsListData?, private val context: Context) : BindableItem<AvailablePartsRowBinding>() {

    override fun bind(viewBinding: AvailablePartsRowBinding, position: Int) {

        viewBinding.apply {

            txtPartName.text = availablePartsData?.PartName
            txtQty.text = availablePartsData?.ActualQuantity

        }
    }

    override fun getLayout() = R.layout.available_parts_row


    override fun initializeViewBinding(view: View) = AvailablePartsRowBinding.bind(view)


}