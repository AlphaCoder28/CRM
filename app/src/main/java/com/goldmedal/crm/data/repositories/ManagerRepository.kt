package com.goldmedal.crm.data.repositories

import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.SafeApiRequest
import com.goldmedal.crm.data.network.responses.NotificationFeedsResponse
import com.goldmedal.crm.ui.dashboard.Manager.Data
import com.goldmedal.crm.ui.dashboard.Manager.ManagerTicketCountData

class ManagerRepository(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {

     suspend fun managerTicketCount(intUserID: Int,intServiceCenterID:Int): ManagerTicketCountData {
        return apiRequest {
            api.getManagerTicketCount(intUserID, intServiceCenterID)
        }
    }
   }