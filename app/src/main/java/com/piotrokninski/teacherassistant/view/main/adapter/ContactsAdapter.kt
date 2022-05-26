package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.ContactFriendInvitationListItemBinding
import com.piotrokninski.teacherassistant.databinding.ContactFriendListItemBinding
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.ContactAdapterItem
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.AppConstants

class ContactsAdapter(
    private val context: Context,
    private val clickListener: (ContactAdapterItem) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contactItems = ArrayList<ContactAdapterItem>()

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
                    contactItems[position] as ContactAdapterItem.FriendAdapterItem,
                    clickListener
                )
            }

            AppConstants.CONTACT_FRIEND_INVITATION_ITEM -> {
                (holder as FriendInvitationViewHolder).bind(
                    context,
                    contactItems[position] as ContactAdapterItem.FriendInvitationAdapterItem,
                    clickListener
                )
            }

            AppConstants.HEADER_ITEM -> {
                (holder as HeaderViewHolder).bind(
                    context,
                    contactItems[position] as ContactAdapterItem.HeaderAdapterItem
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return contactItems.size
    }

    fun setContactItems(contactAdapterItems: List<ContactAdapterItem>) {
        this.contactItems.clear()
        this.contactItems.addAll(contactAdapterItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (contactItems[position]) {
            is ContactAdapterItem.FriendAdapterItem -> AppConstants.CONTACT_FRIEND_ITEM
            is ContactAdapterItem.FriendInvitationAdapterItem -> AppConstants.CONTACT_FRIEND_INVITATION_ITEM
            is ContactAdapterItem.HeaderAdapterItem -> AppConstants.HEADER_ITEM
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

        fun bind(
            friendAdapterItem: ContactAdapterItem.FriendAdapterItem,
            clickListener: (ContactAdapterItem.FriendAdapterItem) -> Unit
        ) {
            binding.friendItem = friendAdapterItem
            binding.contactItemLayout.setOnClickListener { clickListener(friendAdapterItem) }
            if (friendAdapterItem.read) {
                binding.contactFriendItemFullName.typeface = Typeface.DEFAULT_BOLD
                binding.contactFriendItemMessage.typeface = Typeface.DEFAULT_BOLD
            }
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

        fun bind(
            context: Context,
            friendInvitationAdapterItem: ContactAdapterItem.FriendInvitationAdapterItem,
            clickListener: (ContactAdapterItem.FriendInvitationAdapterItem) -> Unit
        ) {
            binding.friendInvitationItem = friendInvitationAdapterItem

            val fullName = when (friendInvitationAdapterItem.sentReceived) {
                AppConstants.SENT_INVITATIONS -> friendInvitationAdapterItem.friendInvitation.invitedUserFullName

                AppConstants.RECEIVED_INVITATIONS -> friendInvitationAdapterItem.friendInvitation.invitingUserFullName

                else -> throw IllegalArgumentException("The argument is not valid")
            }

            binding.contactFriendInvitationFullName.text =
                when (friendInvitationAdapterItem.friendInvitation.invitationType) {
                    FriendInvitation.TYPE_STUDENT -> context.getString(
                        R.string.friend_invitation_item_title_student,
                        fullName
                    )

                    FriendInvitation.TYPE_TUTOR -> context.getString(
                        R.string.friend_invitation_item_title_tutor,
                        fullName
                    )

                    FriendInvitation.TYPE_FRIEND -> context.getString(
                        R.string.friend_invitation_item_title_friend,
                        fullName
                    )

                    else -> throw IllegalArgumentException("The argument is not valid")
                }

            binding.contactFriendInvitationLayout.setOnClickListener {
                clickListener(
                    friendInvitationAdapterItem
                )
            }
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

        fun bind(context: Context, headerAdapterItem: ContactAdapterItem.HeaderAdapterItem) {
            binding.headerItemTitle.text = context.getString(headerAdapterItem.titleId)
        }
    }
}