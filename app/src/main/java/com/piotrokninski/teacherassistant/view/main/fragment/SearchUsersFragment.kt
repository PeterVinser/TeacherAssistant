package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentSearchUsersBinding
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.SearchUsersAdapter
import com.piotrokninski.teacherassistant.viewmodel.main.SearchUsersViewModel

class SearchUsersFragment : Fragment() {
    private val TAG = "SearchUsersFragment"

    private lateinit var binding: FragmentSearchUsersBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchUsersAdapter

    private lateinit var searchUsersViewModel: SearchUsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)

        recyclerView = binding.searchUsersRecyclerView

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)

                (menu.findItem(R.id.users_search_view).actionView as SearchView).isIconified = false

                (menu.findItem(R.id.users_search_view).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchUsersViewModel.setSearchMode(AppConstants.PROFILES_SEARCH_MODE)
                        if (query != null) {
                            searchUsersViewModel.searchUsers(query.lowercase())
                            (activity as MainActivity).hideKeyboard()
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        searchUsersViewModel.setSearchMode(AppConstants.HINTS_SEARCH_MODE)
                        if (newText != null) {
                            searchUsersViewModel.searchUsers(newText.lowercase())
                            Log.d(TAG, "onQueryTextChange: $newText")
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        initRecyclerView()

        setupViewModel()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchUsersAdapter {
                searchUsersAdapterItem: SearchUsersAdapter.Item -> searchedUserClicked(searchUsersAdapterItem)
        }
        recyclerView.adapter = adapter
    }

    private fun searchedUserClicked(searchUserAdapterItem: SearchUsersAdapter.Item) {
        (activity as MainActivity).hideKeyboard()
        val action = SearchUsersFragmentDirections.actionSearchedUsersToUserProfile(searchUserAdapterItem.id)
        this.findNavController().navigate(action)
    }

    private fun setupViewModel() {
        val factory = SearchUsersViewModel.Factory()
        searchUsersViewModel = ViewModelProvider(this, factory)[SearchUsersViewModel::class.java]

        searchUsersViewModel.mSearchUsersItemsAdapter.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                binding.searchUsersNotFound.visibility = View.GONE
                Log.d(TAG, "setupViewModel: list is not empty")
            } else {
                recyclerView.visibility = View.GONE
                binding.searchUsersNotFound.visibility = View.VISIBLE
                Log.d(TAG, "setupViewModel: list is empty")
            }
            adapter.setSearchedUsers(it)
        }
    }
}