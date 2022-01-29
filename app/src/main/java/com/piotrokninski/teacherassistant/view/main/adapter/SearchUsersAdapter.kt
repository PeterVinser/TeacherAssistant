package com.piotrokninski.teacherassistant.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.UserHintListItemBinding
import com.piotrokninski.teacherassistant.databinding.UserProfileListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.SearchUserAdapterItem
import com.piotrokninski.teacherassistant.util.AppConstants

class SearchUsersAdapter(private val clickListener: (SearchUserAdapterItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "SearchUsersAdapter"

    private val searchedUsers = ArrayList<SearchUserAdapterItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            AppConstants.HINTS_SEARCH_MODE -> {
                UserHintViewHolder(UserHintViewHolder.initBinding(parent))
            }
            AppConstants.PROFILES_SEARCH_MODE -> {
                UserProfileViewHolder(UserProfileViewHolder.initBinding(parent))
            }
            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            AppConstants.HINTS_SEARCH_MODE -> {
                (holder as UserHintViewHolder).bind(
                    searchedUsers[position] as SearchUserAdapterItem.UserAdapterHint,
                    clickListener
                )
            }

            AppConstants.PROFILES_SEARCH_MODE -> {
                (holder as UserProfileViewHolder).bind(
                    searchedUsers[position] as SearchUserAdapterItem.UserAdapterProfile,
                    clickListener
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return searchedUsers.size
    }

    fun setSearchedUsers(searchUserAdapters: List<SearchUserAdapterItem>) {
        this.searchedUsers.clear()
        this.searchedUsers.addAll(searchUserAdapters)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (searchedUsers[position]) {
            is SearchUserAdapterItem.UserAdapterHint -> AppConstants.HINTS_SEARCH_MODE
            is SearchUserAdapterItem.UserAdapterProfile -> AppConstants.PROFILES_SEARCH_MODE
        }
    }

    class UserHintViewHolder(private val binding: UserHintListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): UserHintListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return UserHintListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            userAdapterHint: SearchUserAdapterItem.UserAdapterHint,
            clickListener: (SearchUserAdapterItem) -> Unit
        ) {
            binding.userHint = userAdapterHint
            binding.userHintItemCardView.setOnClickListener { clickListener(userAdapterHint) }
        }
    }

    class UserProfileViewHolder(private val binding: UserProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun initBinding(parent: ViewGroup): UserProfileListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return UserProfileListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            userAdapterProfile: SearchUserAdapterItem.UserAdapterProfile,
            clickListener: (SearchUserAdapterItem) -> Unit
        ) {
            binding.userProfile = userAdapterProfile
            binding.userProfileItemCardView.setOnClickListener { clickListener(userAdapterProfile) }
        }
    }
}