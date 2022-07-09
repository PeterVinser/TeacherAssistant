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
import com.piotrokninski.teacherassistant.databinding.FragmentHomeBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.HomeAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.HomeFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.HomeFragmentViewModelFactory

class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"

    private lateinit var binding: FragmentHomeBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter

    private lateinit var homeViewModel: HomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        recyclerView = binding.homeRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(true)

        arguments?.let {
            val userId = it.getString(FriendInvitation.INVITING_USER_ID)
            if (userId != null) {
                navigateToProfile(userId)
            }
        }

        setupViewModel()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = HomeAdapter(
            { homeAdapterItem: HomeAdapterItem -> itemClickListener(homeAdapterItem) },
            { homeAdapterItem: HomeAdapterItem ->
                homeViewModel.itemPositiveAction(homeAdapterItem)
            },
            { homeAdapterItem: HomeAdapterItem ->
                homeViewModel.itemNegativeAction(homeAdapterItem)
            },
            homeViewModel.viewType,
            requireContext()
        )
        recyclerView.adapter = adapter
    }

    private fun itemClickListener(homeAdapterItem: HomeAdapterItem) {
        when (homeAdapterItem) {
            is HomeAdapterItem.InvitationItem -> navigateToProfile(homeAdapterItem.invitation.invitingUserId)

            is HomeAdapterItem.HomeworkItem -> {}

            else -> {}
        }
    }

    private fun navigateToProfile(userId: String) {
        val action = HomeFragmentDirections.actionHomeToUserProfile(userId)
        this.findNavController().navigate(action)
    }

    private fun setupViewModel() {
        val factory = HomeFragmentViewModelFactory()
        homeViewModel = ViewModelProvider(this, factory)[HomeFragmentViewModel::class.java]

        initRecyclerView()

        homeViewModel.homeFeedItems.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }


        lifecycleScope.launchWhenCreated {
            homeViewModel.eventFlow.collect { event ->
                when (event) {
                    is HomeFragmentViewModel.HomeEvent.EditItemEvent -> editItem(event.homeAdapterItem)
                }
            }
        }
    }

    private fun editItem(item: HomeAdapterItem) {
        val action = when (item) {
            is HomeAdapterItem.InvitationItem ->
                HomeFragmentDirections.actionHomeToInvitation(item.invitation.type, item.invitation)

            else -> null
        }

        if (action != null) this.findNavController().navigate(action)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_toolbar_destination_user_account -> {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_home_to_user)
                true
            }
            R.id.menu_toolbar_destination_search -> {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_home_to_searchUsers)
                true
            }
            R.id.menu_toolbar_destination_calendar -> {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_home_to_calendar)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}