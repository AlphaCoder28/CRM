package com.goldmedal.crm.ui.dashboard.Manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.data.repositories.ManagerRepository
import com.goldmedal.crm.data.repositories.NotificationRepository
import com.goldmedal.crm.data.repositories.UserRepository
import com.goldmedal.crm.ui.auth.LoginViewModel
import com.goldmedal.crm.ui.dashboard.notification.NotificationViewModel

@Suppress("UNCHECKED_CAST")
class ManagerViewModelFactory(
        private val repository: ManagerRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ManagerViewModel(repository) as T
    }
}