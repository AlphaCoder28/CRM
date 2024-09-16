package com.goldmedal.crm.ui.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.data.repositories.TicketRepository

@Suppress("UNCHECKED_CAST")
class TicketViewModelFactory(
        private val repository: TicketRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TicketViewModel(repository) as T
    }
}