package com.goldmedal.crm.ui.dashboard.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.repositories.NotificationRepository
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException

class NotificationViewModel(
        private val repository: NotificationRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getLoggedInUser()


    var apiListener: ApiStageListener<Any>? = null


    fun fetchNotifications(userID: Int?) {

//        apiListener?.onStarted("notification_feeds")
//
//        Coroutines.main {
//            try {
//
//
//                val feedsResponse = userID?.let { repository.fetchNotifications(it) }
//
//                if (!feedsResponse?.feeds?.isNullOrEmpty()!!) {
//                    feedsResponse.feeds.let {
//                        apiListener?.onSuccess(it, "notification_feeds")
//
//                        //repository.removeAllHolidaysData()
//                        //repository.saveAllHolidaysData(it)
//                        print("inside all holiday- - - " + it.size)
//                        return@main
//                    }
//                }
//
//                apiListener?.onError(feedsResponse.StatusCodeMessage!!, "notification_feeds")
//            } catch (e: ApiException) {
//                apiListener?.onError(e.message!!, "notification_feeds")
//            } catch (e: NoInternetException) {
//
//
//                apiListener?.onError(e.message!!, "notification_feeds")
//                print("Internet not available")
//            }
//        }

    }
}