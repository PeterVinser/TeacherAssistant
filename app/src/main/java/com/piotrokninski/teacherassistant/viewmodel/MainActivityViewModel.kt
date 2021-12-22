package com.piotrokninski.teacherassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    init {
        viewModelScope.launch {
        }
    }

    fun updateViewType() {

    }
}