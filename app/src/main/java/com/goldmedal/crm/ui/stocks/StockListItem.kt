package com.goldmedal.crm.ui.stocks

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.StockItemData
import com.goldmedal.crm.databinding.StockListRowBinding
import com.xwray.groupie.viewbinding.BindableItem

class StockListItem(
    private val stockListData: StockItemData?,
    private val context: Context
) : BindableItem<StockListRowBinding>() {
    override fun bind(viewBinding: StockListRowBinding, position: Int) {
        viewBinding.apply {
            tvStockListRowQtyValue.text = stockListData?.qty.toString()
            tvStockListRowItemValue.text = stockListData?.itemName
        }
    }

    override fun getLayout() = R.layout.stock_list_row

    override fun initializeViewBinding(view: View) = StockListRowBinding.bind(view)
}