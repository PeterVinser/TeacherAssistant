package com.piotrokninski.teacherassistant.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.UserHintListItemBinding
import com.piotrokninski.teacherassistant.databinding.UserProfileListItemBinding
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.util.AppConstants

class SearchUsersAdapter(private val clickListener: (Item) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "SearchUsersAdapter"

    private val searchedUsers = ArrayList<Item>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            Item.Hint.ID ->
                UserHintViewHolder(UserHintViewHolder.initBinding(parent))

            Item.Profile.ID ->
                UserProfileViewHolder(UserProfileViewHolder.initBinding(parent))

            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            AppConstants.HINTS_SEARCH_MODE -> {
                (holder as UserHintViewHolder).bind(
                    searchedUsers[position] as Item.Hint,
                    clickListener
                )
            }

            AppConstants.PROFILES_SEARCH_MODE -> {
                (holder as UserProfileViewHolder).bind(
                    searchedUsers[position] as Item.Profile,
                    clickListener
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return searchedUsers.size
    }

    fun setSearchedUsers(searchUserAdapters: List<Item>) {
        this.searchedUsers.clear()
        this.searchedUsers.addAll(searchUserAdapters)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (searchedUsers[position]) {
            is Item.Hint -> Item.Hint.ID
            is Item.Profile -> Item.Profile.ID
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
            userAdapterHint: Item.Hint,
            clickListener: (Item) -> Unit
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
            userAdapterProfile: Item.Profile,
            clickListener: (Item) -> Unit
        ) {
            binding.userProfile = userAdapterProfile
            binding.userProfileItemCardView.setOnClickListener { clickListener(userAdapterProfile) }
        }
    }

    sealed class Item {

        abstract val id: String

        data class Hint(val userId: String, val fullName: String): Item() {
            override val id = userId

            companion object {
                const val ID = 1
            }
        }

        data class Profile(val user: User): Item() {
            override val id = user.userId

            companion object {
                const val ID = 2
            }
        }
    }
}