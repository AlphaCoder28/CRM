//package com.goldmedal.crm.ui.invoice
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.goldmedal.crm.data.repositories.HomeRepository
//
//@Suppress("UNCHECKED_CAST")
//class InvoiceViewModelFactory(
//    private val repository: HomeRepository
//) : ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return InvoiceViewModel(repository) as T
//    }
//}