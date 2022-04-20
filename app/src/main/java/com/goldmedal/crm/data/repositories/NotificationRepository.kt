package com.goldmedal.crm.data.repositories

import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.SafeApiRequest
import com.goldmedal.crm.data.network.responses.NotificationFeedsResponse

class NotificationRepository(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {

    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()




    suspend fun fetchNotifications(userId: Int): List<NotificationFeedsResponse> {
        return apiRequest {
            api.fetchNotifications(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }
   }