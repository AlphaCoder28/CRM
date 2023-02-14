package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.goldmedal.crm.databinding.ItemWiringDeviceMultiSelectionBinding

class MultiSelectionItemLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var _binding: ItemWiringDeviceMultiSelectionBinding? = null
    val binding get() = _binding!!
    var multiSelectionCallBack: MultiSelectionViewCallBack<Any>? = null

    init {
        context?.let {
            _binding = ItemWiringDeviceMultiSelectionBinding.inflate(LayoutInflater.from(it), this, true)
            /*binding.ivCancel.setOnClickListener {
                removeAllViews()
            }*/
        }
    }
}

interface MultiSelectionViewCallBack<T> {
    fun onCancelClick()
}