package com.piotrokninski.teacherassistant.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.FragmentContactsBinding
import com.piotrokninski.teacherassistant.model.Friend
import com.piotrokninski.teacherassistant.view.main.adapter.ContactsAdapter
import com.piotrokninski.teacherassistant.viewmodel.ContactsFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.factory.ContactsFragmentViewModelFactory

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter

    private lateinit var contactsViewModel: ContactsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactsBinding.inflate(inflater, container, false)

        recyclerView = binding.contactsRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        initRecyclerView()

        setupViewModel()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ContactsAdapter { friend: Friend -> friendClicked(friend) }
        recyclerView.adapter = adapter
    }

    private fun friendClicked(friend: Friend) {
        val action = ContactsFragmentDirections.actionContactsToUserProfile(friend.userId)
        findNavController(this).navigate(action)
    }

    private fun setupViewModel() {
        val factory = ContactsFragmentViewModelFactory()
        contactsViewModel = ViewModelProvider(this, factory).get(ContactsFragmentViewModel::class.java)

        contactsViewModel.friends.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                binding.contactsNotFound.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                binding.contactsNotFound.visibility = View.VISIBLE
            }
            adapter.setFriends(it)
        })
    }

    override fun onResume() {
        super.onResume()
        contactsViewModel.getFriends()
    }
}