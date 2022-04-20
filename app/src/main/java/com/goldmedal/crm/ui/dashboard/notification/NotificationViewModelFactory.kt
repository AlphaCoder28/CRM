package com.goldmedal.crm.ui.dashboard.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.data.repositories.NotificationRepository
import com.goldmedal.crm.data.repositories.UserRepository
import com.goldmedal.crm.ui.auth.LoginViewModel

@Suppress("UNCHECKED_CAST")
class NotificationViewModelFactory(
        private val repository: NotificationRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationViewModel(repository) as T
    }
}