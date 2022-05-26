package com.piotrokninski.teacherassistant.view.main.adapter

import android.provider.Telephony
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.piotrokninski.teacherassistant.databinding.ReceivedMessageItemBinding
import com.piotrokninski.teacherassistant.databinding.SentMessageItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.ChatAdapterItem
import java.lang.IllegalArgumentException

class ChatAdapter(
    private val fetchItems: (Timestamp) -> Unit,
    private val scrollToPosition: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val chatItems = ArrayList<ChatAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RECEIVED_MESSAGE_ITEM -> {
                ReceivedMessageViewHolder(ReceivedMessageViewHolder.initBinding(parent))
            }

            SENT_MESSAGE_ITEM -> {
                SentMessageViewHolder(SentMessageViewHolder.initBinding(parent))
            }

            else -> {
                throw IllegalArgumentException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            RECEIVED_MESSAGE_ITEM -> (holder as ReceivedMessageViewHolder).bind(chatItems[position] as ChatAdapterItem.ReceivedMessage)

            SENT_MESSAGE_ITEM -> (holder as SentMessageViewHolder).bind(chatItems[position] as ChatAdapterItem.SentMessage)
        }

        if (position == chatItems.size - 1) fetchItems(chatItems[position].timeStamp)
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatItems[position]) {
            is ChatAdapterItem.ReceivedMessage -> RECEIVED_MESSAGE_ITEM
            is ChatAdapterItem.SentMessage -> SENT_MESSAGE_ITEM
        }
    }

    fun setChatItems(chatItems: ArrayList<ChatAdapterItem>) {
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

        fun bind(receivedMessageItem: ChatAdapterItem.ReceivedMessage) {
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

        fun bind(sentMessageItem: ChatAdapterItem.SentMessage) {
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

    companion object {
        const val RECEIVED_MESSAGE_ITEM = 0
        const val SENT_MESSAGE_ITEM = 1
    }
}