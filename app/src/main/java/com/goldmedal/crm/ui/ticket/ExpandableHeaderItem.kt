package com.goldmedal.crm.ui.ticket

import android.graphics.drawable.Animatable
import android.graphics.drawable.VectorDrawable
import android.view.View
import androidx.annotation.StringRes
import com.goldmedal.crm.R
import com.goldmedal.crm.databinding.ExpandableItemBinding



import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem


class ExpandableHeaderItem(
                     @StringRes titleStringResId: Int) : TicketHeaderItem(titleStringResId) ,
    ExpandableItem { //BindableItem<ExpandableItemBinding?>()
    //declare  an expandable group
    private  var expandableGroup: ExpandableGroup? = null
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.apply {
//            //bind your views with your viewholder
//            item_expandable_header_title.text = title
//            item_expandable_header_icon.setImageResource(getIcon())
//            //toggle section when header is clicked
//            item_expandable_header_root.setOnClickListener {
//                expandableGroup.onToggleExpanded()
//            }
//        }
//    }
override fun bind(viewBinding: ExpandableItemBinding, position: Int) {
    super.bind(viewBinding, position)

    // Initial icon state -- not animated.
    viewBinding.itemExpandableHeaderIcon.visibility = View.VISIBLE
    viewBinding.itemExpandableHeaderIcon.setImageResource(if (expandableGroup!!.isExpanded) R.drawable.ic_minus else R.drawable.ic_plus)
    viewBinding.itemExpandableHeaderRoot.setOnClickListener {
        expandableGroup!!.onToggleExpanded()
        bindIcon(viewBinding)
    }

//        viewBinding.apply {
//            itemExpandableHeaderTitle.text = titleStringResId
//            itemExpandableHeaderIcon.setImageResource(getIcon())
//            itemExpandableHeaderRoot.setOnClickListener {
//                expandableGroup.onToggleExpanded()
//            }
//        }

}

    private fun bindIcon(viewBinding: ExpandableItemBinding) {
        viewBinding.itemExpandableHeaderIcon.visibility = View.VISIBLE
        viewBinding.itemExpandableHeaderIcon.setImageResource(if (expandableGroup!!.isExpanded) R.drawable.ic_minus else R.drawable.ic_plus)
//        val drawable = viewBinding.itemExpandableHeaderIcon.drawable as VectorDrawable
//        drawable.()
    }
    override fun getLayout()  = R.layout.expandable_item

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

//    private fun getIcon()= if (expandableGroup!!.isExpanded) R.drawable.ic_minus else
//        R.drawable.ic_plus





    override fun initializeViewBinding(view: View): ExpandableItemBinding = ExpandableItemBinding.bind(view)


}