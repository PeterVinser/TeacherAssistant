package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.ContactFriendInvitationListItemBinding
import com.piotrokninski.teacherassistant.databinding.ContactFriendListItemBinding
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.ContactItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.util.AppConstants

class ContactsAdapter(private val context: Context, private val clickListener: (ContactItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contactItems = ArrayList<ContactItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AppConstants.CONTACT_FRIEND_ITEM -> {
                FriendViewHolder(FriendViewHolder.initBinding(parent))
            }

            AppConstants.CONTACT_FRIEND_INVITATION_ITEM -> {
                FriendInvitationViewHolder(FriendInvitationViewHolder.initBinding(parent))
            }

            AppConstants.HEADER_ITEM -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent))
            }

            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            AppConstants.CONTACT_FRIEND_ITEM -> {
                (holder as FriendViewHolder).bind(
                    contactItems[position] as ContactItem.FriendItem,
                    clickListener
                )
            }

            AppConstants.CONTACT_FRIEND_INVITATION_ITEM -> {
                (holder as FriendInvitationViewHolder).bind(
                    context,
                    contactItems[position] as ContactItem.FriendInvitationItem,
                    clickListener
                )
            }

            AppConstants.HEADER_ITEM -> {
                (holder as HeaderViewHolder).bind(
                    context,
                    contactItems[position] as ContactItem.HeaderItem
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return contactItems.size
    }

    fun setContactItems(contactItems: List<ContactItem>) {
        this.contactItems.clear()
        this.contactItems.addAll(contactItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (contactItems[position]) {
            is ContactItem.FriendItem -> AppConstants.CONTACT_FRIEND_ITEM
            is ContactItem.FriendInvitationItem -> AppConstants.CONTACT_FRIEND_INVITATION_ITEM
            is ContactItem.HeaderItem -> AppConstants.HEADER_ITEM
        }
    }

    class FriendViewHolder(private val binding: ContactFriendListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): ContactFriendListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return ContactFriendListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(friendItem: ContactItem.FriendItem, clickListener: (ContactItem.FriendItem) -> Unit) {
            binding.friendItem = friendItem
            binding.contactItemLayout.setOnClickListener { clickListener(friendItem) }
        }
    }

    class FriendInvitationViewHolder(private val binding: ContactFriendInvitationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): ContactFriendInvitationListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return ContactFriendInvitationListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(context: Context, friendInvitationItem: ContactItem.FriendInvitationItem, clickListener: (ContactItem.FriendInvitationItem) -> Unit) {
            binding.friendInvitationItem = friendInvitationItem

            val fullName = when (friendInvitationItem.sentReceived) {
                AppConstants.SENT_INVITATIONS -> friendInvitationItem.friendInvitation.invitedUserFullName

                AppConstants.RECEIVED_INVITATIONS -> friendInvitationItem.friendInvitation.invitingUserFullName

                else -> throw IllegalArgumentException("The argument is not valid")
            }

            binding.contactFriendInvitationFullName.text = when (friendInvitationItem.friendInvitation.invitationType) {
                FirestoreFriendInvitationContract.TYPE_STUDENT -> context.getString(R.string.friend_invitaiton_item_title_student, fullName)

                FirestoreFriendInvitationContract.TYPE_TUTOR -> context.getString(R.string.friend_invitaiton_item_title_tutor, fullName)

                FirestoreFriendInvitationContract.TYPE_FRIEND -> context.getString(R.string.friend_invitaiton_item_title_friend, fullName)

                else -> throw IllegalArgumentException("The argument is not valid")
            }

            binding.contactFriendInvitationLayout.setOnClickListener { clickListener(friendInvitationItem) }
        }
    }

    class HeaderViewHolder(private val binding: HeaderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HeaderListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HeaderListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(context: Context, headerItem: ContactItem.HeaderItem) {
            binding.headerItemTitle.text = context.getString(headerItem.titleId)
        }
    }
}