package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.*
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.SearchUsersAdapter
import kotlinx.coroutines.launch

class SearchUsersViewModel: ViewModel() {
    private val TAG = "SearchUsersViewModel"

    private var searchMode = AppConstants.HINTS_SEARCH_MODE
    fun setSearchMode(searchMode: Int) {
        this.searchMode = searchMode
    }

    private val _searchedUsersItems = MutableLiveData<List<SearchUsersAdapter.Item>>()
    val mSearchUsersItemsAdapter: LiveData<List<SearchUsersAdapter.Item>> = _searchedUsersItems

    init {
        _searchedUsersItems.value = ArrayList()
    }

    fun searchUsers(keyword: String) {
        viewModelScope.launch {
            val hints = FirestoreUserHintRepository.getUsersHints(keyword)

            val auxList = ArrayList<SearchUsersAdapter.Item>()

            if (searchMode == AppConstants.HINTS_SEARCH_MODE) {

                hints.forEach { user ->
                    auxList.add(SearchUsersAdapter.Item.Hint(user.userId, user.fullName))

                    Log.d(TAG, "searchUsers: ${user.fullName}")
                }

            } else if (searchMode == AppConstants.PROFILES_SEARCH_MODE) {

                val users = FirestoreUserRepository.getUsers(hints)

                users.forEach { user ->
                    auxList.add(SearchUsersAdapter.Item.Profile(user))

                    Log.d(TAG, "searchUsers: ${user.fullName}")
                }

            }

            _searchedUsersItems.value = auxList
        }
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchUsersViewModel::class.java)) {
                return SearchUsersViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}