package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.adapteritem.ChatAdapterItem
import com.piotrokninski.teacherassistant.model.chat.Message
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreChatRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMessageRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChatFragmentViewModel(private val friend: Friend): ViewModel() {
    private val TAG = "ChatFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val chatId = friend.chatId

    private var currentUser: User? = null

    private val _chatItems = MutableLiveData<ArrayList<ChatAdapterItem>>()
    val chatItems: LiveData<ArrayList<ChatAdapterItem>> = _chatItems

    init {
        viewModelScope.launch {
            currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId)
            getMessages()
        }
    }

    private suspend fun getMessages() {
        FirestoreMessageRepository.getLatestMessages(chatId)?.let { messages ->
            updateMessages(messages)

            messages.filter { !it.read && it.senderId != currentUserId }.forEach { unreadMessage ->
                unreadMessage.id?.let {
                    FirestoreMessageRepository.markAsRead(chatId, it)
                }
            }
        }
    }

    fun fetchMeetingsBefore(timestamp: Timestamp) {
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
                currentUserId -> items.add(ChatAdapterItem.SentMessage(message))

                friend.userId -> items.add(ChatAdapterItem.ReceivedMessage(message))
            }
        }

        _chatItems.value = items
    }

    fun sendMessage(text: String) {
        currentUser?.let { it ->
            val message = Message(text, currentUserId, it.fullName, null, Timestamp.now(), false)

            FirestoreMessageRepository.addMessage(chatId, message)

            val newItem = ChatAdapterItem.SentMessage(message)

            val items = arrayListOf<ChatAdapterItem>(newItem)

            _chatItems.value = chatItems.value?.let {
                items.addAll(it)
                items
            } ?: items
        }
    }
}