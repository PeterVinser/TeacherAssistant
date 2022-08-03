package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreChatRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.HomeAdapter
import kotlinx.coroutines.launch

class HomeViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private var viewType: String? = null

    private val _titleId = MutableLiveData<Int>()
    val titleId: LiveData<Int> = _titleId

    private val _chatItems = MutableLiveData<List<HomeAdapter.ChatItem>>()
    val chatItems: LiveData<List<HomeAdapter.ChatItem>> = _chatItems

    init {
        viewModelScope.launch {
            viewType = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)

             _titleId.value = when(viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> R.string.tutors_label

                AppConstants.VIEW_TYPE_TUTOR -> R.string.students_label

                else -> null
            }

            getChats()
        }
    }

    private suspend fun getChats() {
        val chatItems = ArrayList<HomeAdapter.ChatItem>()

        val type = when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT -> Chat.Contract.TUTOR
            AppConstants.VIEW_TYPE_TUTOR -> Chat.Contract.STUDENT
            else -> null
        }

        if (type != null) {

            val chats = FirestoreChatRepository.getChats(currentUserId, type)

            chats?.forEach { chat ->
                val fullName = chat.fullNames.minus(currentUserId).values.first()

                Log.d(TAG, "getFriends: ${chat.latestMessage?.toSnapshot()}")

                val read = chat.read || currentUserId == chat.latestMessage?.senderId
                chatItems.add(HomeAdapter.ChatItem(chat, fullName, read))
            }

            _chatItems.value = chatItems
        }
    }

    fun markAsRead(chatId: String) {
        FirestoreChatRepository.updateChat(chatId, Chat.Contract.READ, true)
    }

    class Factory(private val repository: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}
