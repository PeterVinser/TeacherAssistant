package com.piotrokninski.teacherassistant.view.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.UserHintListItemBinding
import com.piotrokninski.teacherassistant.databinding.UserProfileListItemBinding
import com.piotrokninski.teacherassistant.model.SearchedUserItem
import com.piotrokninski.teacherassistant.util.AppConstants

class SearchUsersAdapter(private val clickListener: (SearchedUserItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "SearchUsersAdapter"

    private val searchedUsers = ArrayList<SearchedUserItem>()

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
        if (holder.itemViewType == AppConstants.HINTS_SEARCH_MODE) {
            (holder as UserHintViewHolder).bind(
                searchedUsers[position] as SearchedUserItem.UserHint,
                clickListener
            )
        }
        if (holder.itemViewType == AppConstants.PROFILES_SEARCH_MODE) {
            (holder as UserProfileViewHolder).bind(
                searchedUsers[position] as SearchedUserItem.UserProfile,
                clickListener
            )
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${searchedUsers.size}")
        return searchedUsers.size
    }

    fun setSearchedUsers(searchedUsers: List<SearchedUserItem>) {
        this.searchedUsers.clear()
        this.searchedUsers.addAll(searchedUsers)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (searchedUsers[position]) {
            is SearchedUserItem.UserHint -> AppConstants.HINTS_SEARCH_MODE
            is SearchedUserItem.UserProfile -> AppConstants.PROFILES_SEARCH_MODE
        }
    }

    class UserHintViewHolder(private val binding: UserHintListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): UserHintListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.user_hint_list_item,
                    parent,
                    false
                ) as UserHintListItemBinding
            }
        }

        fun bind(
            userHint: SearchedUserItem.UserHint,
            clickListener: (SearchedUserItem) -> Unit
        ) {
            binding.userHint = userHint
            binding.userHintItemCardView.setOnClickListener { clickListener(userHint) }
        }
    }

    class UserProfileViewHolder(private val binding: UserProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun initBinding(parent: ViewGroup): UserProfileListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.user_profile_list_item,
                    parent,
                    false
                ) as UserProfileListItemBinding
            }
        }

        fun bind(
            userProfile: SearchedUserItem.UserProfile,
            clickListener: (SearchedUserItem) -> Unit
        ) {
            binding.userProfile = userProfile
            binding.userProfileItemCardView.setOnClickListener { clickListener(userProfile) }
        }
    }
}