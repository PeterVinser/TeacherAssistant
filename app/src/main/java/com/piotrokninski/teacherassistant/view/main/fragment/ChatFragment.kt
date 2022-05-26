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
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.CalendarAdapter
import com.piotrokninski.teacherassistant.view.main.adapter.ChatAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.ChatFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.ChatFragmentViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var chatViewModel: ChatFragmentViewModel

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
            val friend = safeArgs.friend
            setupViewModel(friend)
            (activity as MainActivity).setToolbarTitle(friend.fullName)
        }

        binding.chatSendButton.setOnClickListener { onSendClicked() }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = false
        recyclerView.layoutManager = linearLayoutManager
        adapter = ChatAdapter({ timestamp ->
            chatViewModel.fetchMeetingsBefore(timestamp)
        }, { position ->
            recyclerView.smoothScrollToPosition(position)
        })
        recyclerView.adapter = adapter
    }

    private fun setupViewModel(friend: Friend) {
        val factory = ChatFragmentViewModelFactory(friend)

        chatViewModel = ViewModelProvider(this, factory)[ChatFragmentViewModel::class.java]

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