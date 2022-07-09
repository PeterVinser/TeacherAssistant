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
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.AppConstants

class ContactsAdapter(
    private val clickListener: (ContactAdapterItem.FriendAdapterItem) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.FriendViewHolder>() {

    private val contactItems = ArrayList<ContactAdapterItem.FriendAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ContactFriendListItemBinding.inflate(layoutInflater, parent, false)

        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(contactItems[position], clickListener)
    }

    override fun getItemCount(): Int {
        return contactItems.size
    }

    fun setContactItems(contactAdapterItems: List<ContactAdapterItem.FriendAdapterItem>) {
        this.contactItems.clear()
        this.contactItems.addAll(contactAdapterItems)
        notifyDataSetChanged()
    }

    class FriendViewHolder(private val binding: ContactFriendListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            friendItem: ContactAdapterItem.FriendAdapterItem,
            clickListener: (ContactAdapterItem.FriendAdapterItem) -> Unit
        ) {
            binding.friendItem = friendItem
            binding.contactItemLayout.setOnClickListener { clickListener(friendItem) }
            if (friendItem.read) {
                binding.contactFriendItemFullName.typeface = Typeface.DEFAULT_BOLD
                binding.contactFriendItemMessage.typeface = Typeface.DEFAULT_BOLD
            }
        }
    }
}