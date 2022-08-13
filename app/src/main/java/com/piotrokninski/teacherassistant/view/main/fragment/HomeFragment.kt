package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentHomeBinding
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
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

        recyclerView = binding.homeRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).isBottomNavVisible(true)

        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.destination_user_account -> {
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_home_to_user)
                        true
                    }
                    R.id.destination_search -> {
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_home_to_searchUsers)
                        true
                    }
                    R.id.destination_invitations -> {
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_home_to_invitations)
                        true
                    }
                    R.id.destination_calendar -> {
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                            .navigate(R.id.action_home_to_calendar)
                        true
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
            homeViewModel.markAsRead(chatItem)

            HomeFragmentDirections.actionHomeToChat(chatItem).let { action ->
                this.findNavController().navigate(action)
            }
        } else {
            Toast.makeText(activity, "Czat niedostępny", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        val factory = HomeViewModel.Factory(DataStoreRepository(requireContext()))
        homeViewModel =
            ViewModelProvider(this, factory)[HomeViewModel::class.java]

        observeContactItems()
        observeTitle()
    }

    private fun observeContactItems() {
        homeViewModel.chatItems.observe(viewLifecycleOwner) { chatItems ->
            if (chatItems.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                binding.homeChatsNotFound.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                binding.homeChatsNotFound.visibility = View.VISIBLE
            }
            adapter.setChatItems(chatItems)
        }
    }

    private fun observeTitle() {
        homeViewModel.titleId.observe(viewLifecycleOwner) { titleId ->
            if (titleId != null) {
                (activity as MainActivity).setToolbarTitle(getString(titleId))
            }
        }
    }
}