package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentContactsBinding
import com.piotrokninski.teacherassistant.model.adapteritem.ContactAdapterItem
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.ContactsAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.ContactsFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.ContactsFragmentViewModelFactory

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

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
        (activity as MainActivity).isBottomNavVisible(true)

        initRecyclerView()

        setupViewModel()

        binding.contactsFriendsToggleButton.isChecked = true

        binding.contactsToggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onToggleButtonClicked(
                checkedId,
                isChecked
            )
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ContactsAdapter (requireContext()) { contactAdapterItem: ContactAdapterItem ->
            contactItemClicked(
                contactAdapterItem
            )
        }
        recyclerView.adapter = adapter
    }

    private fun contactItemClicked(contactAdapterItem: ContactAdapterItem) {
        when (contactAdapterItem) {
            is ContactAdapterItem.FriendAdapterItem -> {
                val action = ContactsFragmentDirections.actionContactsToUserProfile(contactAdapterItem.userId)
                findNavController(this).navigate(action)
            }

            is ContactAdapterItem.FriendInvitationAdapterItem -> {
                val userId = when (contactAdapterItem.sentReceived) {
                    AppConstants.SENT_INVITATIONS -> {
                        contactAdapterItem.friendInvitation.invitedUserId
                    }

                    AppConstants.RECEIVED_INVITATIONS -> {
                        contactAdapterItem.friendInvitation.invitingUserId
                    }

                    else -> throw IllegalArgumentException("Not a valid argument")
                }

                val action = ContactsFragmentDirections.actionContactsToUserProfile(userId)
                findNavController(this).navigate(action)
            }

            is ContactAdapterItem.HeaderAdapterItem -> {}
        }
    }

    private fun setupViewModel() {
        val factory = ContactsFragmentViewModelFactory()
        contactsViewModel =
            ViewModelProvider(this, factory).get(ContactsFragmentViewModel::class.java)

        observeContactItems()
    }

    private fun observeContactItems() {
        contactsViewModel.mContactAdapterItems.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                binding.contactsNotFound.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                binding.contactsNotFound.visibility = View.VISIBLE
            }
            (adapter as ContactsAdapter).setContactItems(it)
        }

    }

    private fun onToggleButtonClicked(checkedId: Int, isChecked: Boolean) {
        if (isChecked) {
            when (checkedId) {
                R.id.contacts_friends_toggle_button -> {
                    contactsViewModel.getFriends()
                }

                R.id.contacts_invitations_toggle_button -> {
                    contactsViewModel.getInvitations()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        contactsViewModel.getFriends()
    }
}