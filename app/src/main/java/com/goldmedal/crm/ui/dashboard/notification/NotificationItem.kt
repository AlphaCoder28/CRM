package com.goldmedal.crm.ui.dashboard.notification

import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.NotificationFeedsData
import com.goldmedal.crm.databinding.NotificationRowBinding
//import com.goldmedal.crm.databinding.NotificationRowBinding
import com.xwray.groupie.viewbinding.BindableItem


class NotificationItem(private val feeds: NotificationFeedsData?) : BindableItem<NotificationRowBinding>(){

    override fun bind(viewBinding: NotificationRowBinding, position: Int) {
        viewBinding.feeds = feeds
    }


    override fun getLayout() = R.layout.notification_row
    override fun initializeViewBinding(view: View): NotificationRowBinding  = NotificationRowBinding.bind(view)


}