package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.ContactsFragmentViewModel

class ContactsFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsFragmentViewModel::class.java)) {
            return ContactsFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}