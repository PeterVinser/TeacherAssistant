package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentHomeBinding
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.HomeAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.contactsRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).isBottomNavVisible(true)

        // TODO: move the data logic to view model with channel
        val title = when(MainPreferences.getViewType()) {
            AppConstants.VIEW_TYPE_STUDENT -> getString(R.string.tutors_label)

            AppConstants.VIEW_TYPE_TUTOR -> getString(R.string.students_label)

            else -> throw IllegalArgumentException("Unknown viewType")
        }

        (activity as MainActivity).setToolbarTitle(title)

        initRecyclerView()

        setupViewModel()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = HomeAdapter { chatItem: HomeAdapter.ChatItem ->
            chatItemClicked(
                chatItem
            )
        }
        recyclerView.adapter = adapter
    }

    private fun chatItemClicked(chatItem: HomeAdapter.ChatItem) {
        if (chatItem.chat.id != null) {
            homeViewModel.markAsRead(chatItem.chat.id)

            HomeFragmentDirections.actionContactsToChat(chatItem).let { action ->
                this.findNavController().navigate(action)
            }
        } else {
            Toast.makeText(activity, "Czat niedostępny", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        val factory = HomeViewModel.Factory()
        homeViewModel =
            ViewModelProvider(this, factory)[HomeViewModel::class.java]

        observeContactItems()
    }

    private fun observeContactItems() {
        homeViewModel.chatItems.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                binding.contactsNotFound.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                binding.contactsNotFound.visibility = View.VISIBLE
            }
            adapter.setChatItems(it)
        }

    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getChats()
    }
}