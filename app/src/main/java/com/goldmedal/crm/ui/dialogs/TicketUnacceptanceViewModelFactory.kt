package com.goldmedal.hrapp.ui.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.data.repositories.TicketRepository


@Suppress("UNCHECKED_CAST")
class TicketUnacceptanceViewModelFactory(
        private val repository: TicketRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TicketUnacceptanceViewModel(repository) as T
    }
}