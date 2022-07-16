package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.FragmentChatBinding
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.ChatAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.ChatViewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        recyclerView = binding.chatMessages

        initRecyclerView()

        arguments?.let {
            val safeArgs = ChatFragmentArgs.fromBundle(it)
            val chatItem = safeArgs.chatItem
            setupViewModel(chatItem.chat.id!!)
            (activity as MainActivity).setToolbarTitle(chatItem.fullName)
        }

        binding.chatSendButton.setOnClickListener { onSendClicked() }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = false
        recyclerView.layoutManager = linearLayoutManager
        adapter = ChatAdapter({ timestamp ->
            chatViewModel.fetchMessagesBefore(timestamp)
        }, { position ->
            recyclerView.smoothScrollToPosition(position)
        })
        recyclerView.adapter = adapter
    }

    private fun setupViewModel(chatId: String) {
        val factory = ChatViewModel.Factory(chatId)

        chatViewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        observeChatItems()
    }

    private fun observeChatItems() {
        chatViewModel.chatItems.observe(viewLifecycleOwner) { items ->
            if (!items.isNullOrEmpty()) {
                adapter.setChatItems(items)
            }
        }
    }

    private fun onSendClicked() {
        val text = binding.chatNewMessage.text.toString()

        if (text.isNotEmpty()) chatViewModel.sendMessage(text)

        binding.chatNewMessage.text.clear()
    }
}