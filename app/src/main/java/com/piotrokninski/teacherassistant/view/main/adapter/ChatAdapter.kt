package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.print.PrintAttributes
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.MessageItemBinding
import com.piotrokninski.teacherassistant.databinding.ReceivedMessageItemBinding
import com.piotrokninski.teacherassistant.databinding.SentMessageItemBinding
import com.piotrokninski.teacherassistant.model.chat.Message

class ChatAdapter(
    private val fetchItems: (Timestamp) -> Unit,
    private val scrollToPosition: (Int) -> Unit,
    private val onAttachmentClicked: (String) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val chatItems = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = MessageItemBinding.inflate(layoutInflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(chatItems[position], context, onAttachmentClicked)

        if (position == chatItems.size - 1) fetchItems(chatItems[position].message.timestamp)
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
        if (this.chatItems.firstOrNull()?.message?.timestamp != chatItems.firstOrNull()?.message?.timestamp) {
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

    class MessageViewHolder(private val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, context: Context, onAttachmentClicked: (String) -> Unit) {
            binding.messageItem = item

            if (item.message.attachment != null) {
                binding.messageAttachment.attachment = item.message.attachment

                binding.messageAttachment.root.setOnClickListener {
                    onAttachmentClicked(item.message.attachment!!.itemId)
                }
            }

            when (item) {
                is Item.ReceivedMessage -> {
                    binding.messageLayout.background =
                        context.getDrawable(Item.ReceivedMessage.BACKGROUND_ID)
                    binding.messageParentLayout.gravity = Item.ReceivedMessage.LAYOUT_GRAVITY
//                    binding.messageParentLayout.layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    ).apply { gravity = Item.ReceivedMessage.LAYOUT_GRAVITY }
                }

                is Item.SentMessage -> {
                    binding.messageLayout.background =
                        context.getDrawable(Item.SentMessage.BACKGROUND_ID)
                    binding.messageParentLayout.gravity = Item.SentMessage.LAYOUT_GRAVITY
//                    binding.messageLayout.layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    ).apply { gravity = Item.SentMessage.LAYOUT_GRAVITY }
                }
            }
        }
    }

    sealed class Item {

        abstract val id: String
        abstract val message: Message

        data class ReceivedMessage(override val message: Message) : Item() {
            override val id = message.timestamp.toString()

            companion object {
                const val ID = 0
                const val BACKGROUND_ID = R.drawable.bg_received_message
                const val LAYOUT_GRAVITY = Gravity.START
            }
        }

        data class SentMessage(override val message: Message): Item() {
            override val id = message.timestamp.toString()

            companion object {
                const val ID = 1
                const val BACKGROUND_ID = R.drawable.bg_sent_message
                const val LAYOUT_GRAVITY = Gravity.END
            }
        }
    }
    
    companion object {
        private const val TAG = "ChatAdapter"
    }
}