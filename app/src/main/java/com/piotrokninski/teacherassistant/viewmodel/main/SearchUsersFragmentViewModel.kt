package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.model.adapteritem.SearchUserAdapterItem
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class SearchUsersFragmentViewModel: ViewModel() {
    private val TAG = "SearchUsersViewModel"

    private var searchMode = AppConstants.HINTS_SEARCH_MODE
    fun setSearchMode(searchMode: Int) {
        this.searchMode = searchMode
    }

    private val _searchedUsersItems = MutableLiveData<List<SearchUserAdapterItem>>()
    val mSearchUsersItemsAdapter: LiveData<List<SearchUserAdapterItem>> = _searchedUsersItems

    init {
        _searchedUsersItems.value = ArrayList()
    }

    fun searchUsers(keyword: String) {
        viewModelScope.launch {
            val hints = FirestoreUserHintRepository.getUsersHints(keyword)

            val auxList = ArrayList<SearchUserAdapterItem>()

            if (searchMode == AppConstants.HINTS_SEARCH_MODE) {

                hints.forEach { user ->
                    auxList.add(SearchUserAdapterItem.UserAdapterHint(user.userId, user.fullName))

                    Log.d(TAG, "searchUsers: ${user.fullName}")
                }

            } else if (searchMode == AppConstants.PROFILES_SEARCH_MODE) {

                val users = FirestoreUserRepository.getUsers(hints)

                users.forEach { user ->
                    auxList.add(SearchUserAdapterItem.UserAdapterProfile(user))

                    Log.d(TAG, "searchUsers: ${user.fullName}")
                }

            }

            _searchedUsersItems.value = auxList
        }
    }
}