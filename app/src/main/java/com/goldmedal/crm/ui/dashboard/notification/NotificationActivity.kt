package com.goldmedal.crm.ui.dashboard.notification

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.NotificationFeedsData
import com.goldmedal.crm.databinding.ActivityNotificationBinding
import com.goldmedal.crm.ui.dashboard.TransitionsActivity
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_notification.*

import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class NotificationActivity : TransitionsActivity(), ApiStageListener<Any>, KodeinAware {


    override val kodein by kodein()
    private val factory : NotificationViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_notification)
        val binding: ActivityNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)

        val viewModel = ViewModelProvider(this, factory).get(NotificationViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if(user != null){
                viewModel.fetchNotifications(user.UserId)
            }
        })

    }

    private fun List<NotificationFeedsData?>.toLeaveRecord(): List<NotificationItem?> {
        return this.map {
            NotificationItem(it)
        }
    }

    private fun initRecyclerView(toLeaveRecord: List<NotificationItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }
    private fun bindUI(list: List<NotificationFeedsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveRecord())
        }
    }
    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }




    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()
        bindUI(_object as List<NotificationFeedsData?>)

    }

//    override fun onError(message: String, callFrom: String) {
//        progress_bar?.stop()
//        root_layout.snackbar(message)
//    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        root_layout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        root_layout.snackbar(message)
      //  TODO("Not yet implemented")
    }


}
