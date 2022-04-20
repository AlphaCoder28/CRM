package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.SelectPartsListData
import com.goldmedal.crm.databinding.PartsReqItemRowDialogBinding
import com.xwray.groupie.viewbinding.BindableItem

class PartsRequirementItemDialogRow(private val selectPartListdata: SelectPartsListData?, private val context: Context?) : BindableItem<PartsReqItemRowDialogBinding>() {

    override fun bind(viewBinding: PartsReqItemRowDialogBinding, position: Int) {

        viewBinding.apply {
            txtPartName.text = selectPartListdata?.PartName
            txtPartQty.text = selectPartListdata?.ActualQuantity?.toDouble()?.toInt().toString()
        }
    }

    override fun getLayout() = R.layout.parts_req_item_row_dialog




    override fun initializeViewBinding(view: View) = PartsReqItemRowDialogBinding.bind(view)


}