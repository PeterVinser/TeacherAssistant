package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationsBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.InvitationsAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationsViewModel

class InvitationsFragment : Fragment() {
    private val TAG = "HomeFragment"

    private lateinit var binding: FragmentInvitationsBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InvitationsAdapter

    private lateinit var invitationsViewModel: InvitationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInvitationsBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.invitationsRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        arguments?.let {
            val userId = it.getString(Invitation.Contract.INVITING_USER_ID)
            if (userId != null) {
                navigateToProfile(userId)
            }
        }

        setupViewModel()
    }

    private fun initRecyclerView(viewType: String?) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = InvitationsAdapter(
            { invitationsAdapterItem: InvitationsAdapter.Item -> itemClickListener(invitationsAdapterItem) },
            { invitationsAdapterItem: InvitationsAdapter.Item ->
                invitationsViewModel.itemPositiveAction(invitationsAdapterItem)
            },
            { invitationsAdapterItem: InvitationsAdapter.Item ->
                invitationsViewModel.itemNegativeAction(invitationsAdapterItem)
            },
            requireContext()
        )
        recyclerView.adapter = adapter
    }

    private fun itemClickListener(invitationsAdapterItem: InvitationsAdapter.Item) {
        when (invitationsAdapterItem) {
            is InvitationsAdapter.Item.Invitation -> navigateToProfile(invitationsAdapterItem.invitation.invitingUserId)

            is InvitationsAdapter.Item.Homework -> {}

            else -> {}
        }
    }

    private fun navigateToProfile(userId: String) {
        val action = InvitationsFragmentDirections.actionHomeToUserProfile(userId)
        this.findNavController().navigate(action)
    }

    private fun setupViewModel() {
        val factory = InvitationsViewModel.Factory(DataStoreRepository(requireContext()))
        invitationsViewModel = ViewModelProvider(this, factory)[InvitationsViewModel::class.java]

        initRecyclerView(invitationsViewModel.viewType)

        invitationsViewModel.homeFeedItems.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }


        lifecycleScope.launchWhenCreated {
            invitationsViewModel.eventFlow.collect { event ->
                when (event) {
                    is InvitationsViewModel.HomeEvent.EditItemEvent -> editItem(event.invitationsAdapterItem)
                }
            }
        }
    }

    private fun editItem(item: InvitationsAdapter.Item) {
        val action = when (item) {
            is InvitationsAdapter.Item.Invitation ->
                InvitationsFragmentDirections.actionInvitationsToInvitationDetails(
                    item.invitation.type, item.invitation
                )

            else -> null
        }

        if (action != null) this.findNavController().navigate(action)
    }
}