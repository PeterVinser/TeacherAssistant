package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentSearchUsersBinding
import com.piotrokninski.teacherassistant.model.SearchedUserItem
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.adapter.SearchUsersAdapter
import com.piotrokninski.teacherassistant.viewmodel.SearchUsersFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.factory.SearchUsersFragmentViewModelFactory

class SearchUsersFragment : Fragment() {
    private val TAG = "SearchUsersFragment"

    private lateinit var binding: FragmentSearchUsersBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchUsersAdapter

    private lateinit var searchUsersFragmentViewModel: SearchUsersFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        initRecyclerView()

        setupViewModel()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchUsersAdapter{ searchedUserItem: SearchedUserItem -> searchedUserClicked(searchedUserItem) }
        recyclerView.adapter = adapter
    }

    private fun searchedUserClicked(searchedUserItem: SearchedUserItem) {
        (activity as MainActivity).hideKeyboard()
        val action = SearchUsersFragmentDirections.actionSearchedUsersToUserProfile(searchedUserItem.id)
        findNavController(this).navigate(action)
    }

    private fun setupViewModel() {
        val factory = SearchUsersFragmentViewModelFactory()
        searchUsersFragmentViewModel = ViewModelProvider(this, factory).get(SearchUsersFragmentViewModel::class.java)

        searchUsersFragmentViewModel.searchedUsersItems.observe(viewLifecycleOwner, {
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
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        (menu.findItem(R.id.users_search_view).actionView as SearchView).isIconified = false

        (menu.findItem(R.id.users_search_view).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUsersFragmentViewModel.setSearchMode(AppConstants.PROFILES_SEARCH_MODE)
                if (query != null) {
                    searchUsersFragmentViewModel.searchUsers(query.lowercase())
                    (activity as MainActivity).hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUsersFragmentViewModel.setSearchMode(AppConstants.HINTS_SEARCH_MODE)
                if (newText != null) {
                    searchUsersFragmentViewModel.searchUsers(newText.lowercase())
                    Log.d(TAG, "onQueryTextChange: $newText")
                }
                return true
            }
        })
    }
}