package com.piotrokninski.teacherassistant.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.piotrokninski.teacherassistant.databinding.ReceivedMessageItemBinding
import com.piotrokninski.teacherassistant.databinding.SentMessageItemBinding
import com.piotrokninski.teacherassistant.model.chat.Message

class ChatAdapter(
    private val fetchItems: (Timestamp) -> Unit,
    private val scrollToPosition: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val chatItems = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Item.ReceivedMessage.ID -> {
                ReceivedMessageViewHolder(ReceivedMessageViewHolder.initBinding(parent))
            }

            Item.SentMessage.ID -> {
                SentMessageViewHolder(SentMessageViewHolder.initBinding(parent))
            }

            else -> {
                throw IllegalArgumentException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Item.ReceivedMessage.ID -> (holder as ReceivedMessageViewHolder)
                .bind(chatItems[position] as Item.ReceivedMessage)

            Item.SentMessage.ID -> (holder as SentMessageViewHolder)
                .bind(chatItems[position] as Item.SentMessage)
        }

        if (position == chatItems.size - 1) fetchItems(chatItems[position].timeStamp)
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatItems[position]) {
            is Item.ReceivedMessage -> Item.ReceivedMessage.ID
            is Item.SentMessage -> Item.SentMessage.ID
        }
    }

    fun setChatItems(chatItems: ArrayList<Item>) {
        if (this.chatItems.firstOrNull()?.timeStamp != chatItems.firstOrNull()?.timeStamp) {
            this.chatItems.clear()
            this.chatItems.addAll(chatItems)
            notifyItemInserted(0)
            scrollToPosition(0)
        } else {
            val prevPosition = this.chatItems.size
            this.chatItems.clear()
            this.chatItems.addAll(chatItems)
            notifyItemRangeInserted(prevPosition, chatItems.size)
        }
    }

    class ReceivedMessageViewHolder(private val binding: ReceivedMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(receivedMessageItem: Item.ReceivedMessage) {
            binding.message = receivedMessageItem.message
        }

        companion object {
            fun initBinding(parent: ViewGroup): ReceivedMessageItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return ReceivedMessageItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }
    }


    class SentMessageViewHolder(private val binding: SentMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sentMessageItem: Item.SentMessage) {
            binding.message = sentMessageItem.message
        }

        companion object {
            fun initBinding(parent: ViewGroup): SentMessageItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return SentMessageItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }
    }

    sealed class Item {

        abstract val id: String
        abstract val timeStamp: Timestamp

        data class ReceivedMessage(val message: Message) : Item() {
            override val id = message.timestamp.toString()
            override val timeStamp = message.timestamp

            companion object {
                const val ID = 0
            }
        }

        data class SentMessage(val message: Message): Item() {
            override val id = message.timestamp.toString()
            override val timeStamp = message.timestamp

            companion object {
                const val ID = 1
            }
        }
    }
}