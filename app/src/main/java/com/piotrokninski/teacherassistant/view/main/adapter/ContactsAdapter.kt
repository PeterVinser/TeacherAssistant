package com.piotrokninski.teacherassistant.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.ContactListItemBinding
import com.piotrokninski.teacherassistant.model.Friend

class ContactsAdapter(private val clickListener: (Friend) -> Unit) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private val friends = ArrayList<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            ContactListItemBinding.inflate(layoutInflater,  parent, false)

        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(friends[position], clickListener)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun setFriends(friends: List<Friend>) {
        this.friends.clear()
        this.friends.addAll(friends)
        notifyDataSetChanged()
    }

    class ContactsViewHolder(private val binding: ContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend, clickListener: (Friend) -> Unit) {
            binding.friend = friend
            binding.contactItemLayout.setOnClickListener { clickListener(friend) }
        }
    }
}