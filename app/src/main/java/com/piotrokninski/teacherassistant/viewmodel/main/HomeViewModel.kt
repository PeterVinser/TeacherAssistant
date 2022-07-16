package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreChatRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.HomeAdapter
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val viewType = MainPreferences.getViewType()

    private val _chatItems = MutableLiveData<List<HomeAdapter.ChatItem>>()
    val chatItems: LiveData<List<HomeAdapter.ChatItem>> = _chatItems

    init {
        getChats()
    }

    fun getChats() {
        viewModelScope.launch {

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
    }

    fun markAsRead(chatId: String) {
        FirestoreChatRepository.updateChat(chatId, Chat.Contract.READ, true)
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}
