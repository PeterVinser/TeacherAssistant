package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.chat.Message
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMessageRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.view.main.adapter.ChatAdapter
import kotlinx.coroutines.launch

class ChatViewModel(private val chatId: String): ViewModel() {
    private val TAG = "ChatFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private var currentUser: User? = null

    private val _chatItems = MutableLiveData<ArrayList<ChatAdapter.Item>>()
    val chatItems: LiveData<ArrayList<ChatAdapter.Item>> = _chatItems

    init {
        viewModelScope.launch {
            currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId)
            getMessages()
        }
    }

    private suspend fun getMessages() {
        FirestoreMessageRepository.getLatestMessages(chatId)?.let { messages ->
            updateMessages(messages)
        }
    }

    fun fetchMessagesBefore(timestamp: Timestamp) {
        viewModelScope.launch {
            FirestoreMessageRepository.getMessagesBefore(chatId, timestamp)?.let {
                updateMessages(it)
            }
        }
    }

    private fun updateMessages(messages: ArrayList<Message>) {
        val items = chatItems.value ?: ArrayList()

        messages.forEach { message ->
            when (message.senderId) {
                currentUserId -> items.add(ChatAdapter.Item.SentMessage(message))

                else -> items.add(ChatAdapter.Item.ReceivedMessage(message))
            }
        }

        _chatItems.value = items
    }

    fun sendMessage(text: String) {
        currentUser?.let { it ->
            val message = Message(
                id = null,
                text, currentUserId,
                it.fullName,
                null,
                Timestamp.now()
            )

            FirestoreMessageRepository.addMessage(chatId, message)

            val newItem = ChatAdapter.Item.SentMessage(message)

            val items = arrayListOf<ChatAdapter.Item>(newItem)

            _chatItems.value = chatItems.value?.let {
                items.addAll(it)
                items
            } ?: items
        }
    }

    class Factory(private val chatId: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(chatId) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}