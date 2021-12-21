package com.piotrokninski.teacherassistant.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.SearchUsersFragmentViewModel

class SearchUsersFragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchUsersFragmentViewModel::class.java)) {
            return SearchUsersFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }

}