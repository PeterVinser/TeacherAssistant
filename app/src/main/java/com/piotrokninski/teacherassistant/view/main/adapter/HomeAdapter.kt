package com.piotrokninski.teacherassistant.view.main.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.HomeChatItemBinding
import com.piotrokninski.teacherassistant.model.chat.Chat
import java.io.Serializable
import java.lang.reflect.Type

class HomeAdapter(
    private val clickListener: (ChatItem) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ChatViewHolder>() {

    private val chatItems = ArrayList<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = HomeChatItemBinding.inflate(layoutInflater, parent, false)

        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatItems[position], clickListener)
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }

    fun setChatItems(chatItems: List<ChatItem>) {
        this.chatItems.clear()
        this.chatItems.addAll(chatItems)
        notifyDataSetChanged()
    }

    class ChatViewHolder(private val binding: HomeChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            chatItem: ChatItem,
            clickListener: (ChatItem) -> Unit
        ) {
            binding.chatItem = chatItem
            binding.contactItemLayout.setOnClickListener { clickListener(chatItem) }

            val typeface = if (chatItem.read) Typeface.DEFAULT else Typeface.DEFAULT_BOLD
            binding.contactFriendItemFullName.typeface = typeface
            binding.contactFriendItemMessage.typeface = typeface
        }
    }

    data class ChatItem(
        val chat: Chat,
        val fullName: String,
        val read: Boolean
    ) : Serializable
}