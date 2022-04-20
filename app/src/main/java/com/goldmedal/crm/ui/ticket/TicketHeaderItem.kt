package com.goldmedal.crm.ui.ticket

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.goldmedal.crm.R
import com.goldmedal.crm.databinding.ExpandableItemBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.viewbinding.BindableItem

open class TicketHeaderItem @JvmOverloads constructor(
    @StringRes private val titleStringResId: Int,

    @DrawableRes private val iconResId: Int = 0,
    private val onIconClickListener: View.OnClickListener? = null //@StringRes private val subtitleResId: Int = 0,
) : BindableItem<ExpandableItemBinding?>() {

    override fun getLayout(): Int = R.layout.expandable_item

    override fun initializeViewBinding(view: View): ExpandableItemBinding = ExpandableItemBinding.bind(view)

    override fun bind(viewBinding: ExpandableItemBinding, position: Int) {
        viewBinding.apply {

            itemExpandableHeaderTitle.setText(titleStringResId)


            if (iconResId != 0) {
                itemExpandableHeaderIcon.setImageResource(iconResId)
                itemExpandableHeaderRoot.setOnClickListener(onIconClickListener)
            }

            itemExpandableHeaderIcon.visibility = if (iconResId != 0) View.VISIBLE else View.GONE
//            item_expandable_header_icon.setImageResource(getIcon())
            //toggle section when header is clicked
//            item_expandable_header_root.setOnClickListener {
//                expandableGroup.onToggleExpanded()
//            }
//            title.setText(titleStringResId)
//            if (subtitleResId != 0) {
//                subtitle.setText(subtitleResId)
//            }
//            subtitle.visibility = if (subtitleResId != 0) View.VISIBLE else View.GONE
//
//            if (iconResId != 0) {
//                icon.setImageResource(iconResId)
//                icon.setOnClickListener(onIconClickListener)
//            }
//            icon.visibility = if (iconResId != 0) View.VISIBLE else View.GONE

//                itemExpandableHeaderTitle.text = titleStringResId
//                itemExpandableHeaderIcon.setImageResource(getIcon())
//                itemExpandableHeaderRoot.setOnClickListener {
//                    expandableGroup.onToggleExpanded()
//                }
        }
    }
}