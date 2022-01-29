package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentHomeBinding
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.HomeAdapter
import com.piotrokninski.teacherassistant.view.main.dialog.ReceivedInvitationDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.HomeFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.HomeFragmentViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter

    private lateinit var homeViewModel: HomeFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        this.arguments.let {
            val userId = it?.getString(FirestoreFriendInvitationContract.INVITING_USER_ID)
            if (userId != null) {
                navigateToProfile(userId)
            }
        }
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

        initRecycleView()

        setupViewModel()
    }

    private fun initRecycleView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = HomeAdapter { homeAdapterItem: HomeAdapterItem -> clickListener(homeAdapterItem) }
        recyclerView.adapter = adapter
    }

    private fun clickListener(homeAdapterItem: HomeAdapterItem) {
        if (homeAdapterItem is HomeAdapterItem.Invitation) {

            val dialog = ReceivedInvitationDialogFragment(homeAdapterItem,
                { onProfileClicked(it) },
                { onDetailsClicked(it) },
                { refresh() })

            dialog.show(childFragmentManager, "receivedInvitation")
        }
    }

    fun navigateToProfile(userId: String) {
        val action = HomeFragmentDirections.actionHomeToUserProfile(userId)
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action)
    }

    private fun onProfileClicked(invitation: HomeAdapterItem.Invitation) {
        navigateToProfile(invitation.friendInvitation.invitingUserId)
    }

    private fun onDetailsClicked(invitation: HomeAdapterItem.Invitation) {
        Toast.makeText(activity, "On details clicked", Toast.LENGTH_SHORT).show()
    }

    //TODO make it fucking work
    private fun refresh() {
        homeViewModel.getItems()
    }

    private fun setupViewModel() {
        val factory = HomeFragmentViewModelFactory()
        homeViewModel = ViewModelProvider(this, factory).get(HomeFragmentViewModel::class.java)

        homeViewModel.mHomeAdapterItems.observe(viewLifecycleOwner, {
            adapter.setItems(it)
        })
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

    override fun onResume() {
        super.onResume()
        homeViewModel.getItems()
    }
}