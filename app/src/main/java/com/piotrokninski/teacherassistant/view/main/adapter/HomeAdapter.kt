package com.piotrokninski.teacherassistant.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HomeFriendInvitationListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.HomeFeedItem
import com.piotrokninski.teacherassistant.util.AppConstants
import java.lang.ClassCastException

class HomeAdapter(private val clickListener:(HomeFeedItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<HomeFeedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AppConstants.HOME_FRIEND_INVITATION -> {
                FriendInvitationViewHolder(FriendInvitationViewHolder.initBinding(parent))
            }
            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == AppConstants.HOME_FRIEND_INVITATION) {
            (holder as FriendInvitationViewHolder).bind(
                items[position] as HomeFeedItem.Invitation,
                clickListener
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeFeedItem.Invitation -> AppConstants.HOME_FRIEND_INVITATION
        }
    }

    fun setItems(items: List<HomeFeedItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class FriendInvitationViewHolder(private val binding: HomeFriendInvitationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HomeFriendInvitationListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.home_friend_invitation_list_item,
                    parent,
                    false
                ) as HomeFriendInvitationListItemBinding
            }
        }

        fun bind(
            friendInvitation: HomeFeedItem.Invitation,
            clickListener: (HomeFeedItem) -> Unit
        ) {
            binding.friendInvitation = friendInvitation
            binding.homeFriendInvitation.text = friendInvitation.getInvitationType()
            binding.homeFriendItemLayout.setOnClickListener { clickListener(friendInvitation) }
        }
    }
}