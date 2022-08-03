package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.InvitationListItemBinding
import com.piotrokninski.teacherassistant.model.Invitation

class InvitationsAdapter(
    private val itemClickListener: (Item) -> Unit,
    private val positiveButtonListener: (Item) -> Unit,
    private val negativeButtonListener: (Item) -> Unit,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "HomeAdapter"

    private val items = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            Item.Header.ID -> HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)

            Item.Invitation.ID -> InvitationViewHolder(
                InvitationViewHolder.initBinding(parent),
                context
            )

            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {

            Item.Header.ID -> (holder as HeaderViewHolder).bind(
                items[position] as Item.Header
            )

            Item.Invitation.ID -> (holder as InvitationViewHolder).bind(
                items[position] as Item.Invitation,
                itemClickListener,
                positiveButtonListener,
                negativeButtonListener
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {

            is Item.Header -> Item.Header.ID

            is Item.Invitation -> Item.Invitation.ID

            is Item.Homework -> Item.Homework.ID
        }
    }

    fun setItems(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    private class HeaderViewHolder(
        private val binding: HeaderListItemBinding,
        private val context: Context
    ) :
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

        fun bind(homeHeaderItem: Item.Header) {
            binding.headerItemTitle.text = context.getString(homeHeaderItem.titleId)
        }
    }

    private class InvitationViewHolder(
        private val binding: InvitationListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): InvitationListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return InvitationListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }

            private const val TAG = "HomeAdapter"
        }

        fun bind(
            invitationItem: Item.Invitation,
            itemClickListener: (Item) -> Unit,
            positiveButtonListener: (Item) -> Unit,
            negativeButtonListener: (Item) -> Unit
        ) {
            binding.invitation = invitationItem

            val course = invitationItem.invitation.course
            val meeting = invitationItem.invitation.meeting

            Log.d(TAG, "bind: $meeting")

            // TODO: add the functionality to display invitation details
//            binding.invitationItemLayout.setOnClickListener {
//                itemClickListener(
//                    invitationItem
//                )
//            }

            if (!invitationItem.received) {
                binding.invitationItemConfirmButton.text = context.getString(R.string.edit_button_text)
                binding.invitationItemRejectButton.text = context.getString(R.string.cancel_button_text)
            }

            binding.invitationItemRejectButton.setOnClickListener {
                negativeButtonListener(
                    invitationItem
                )
            }

            binding.invitationItemConfirmButton.setOnClickListener {
                positiveButtonListener(
                    invitationItem
                )
            }

            if (course != null) {

                binding.invitationItemCourseLayout.course = course

                binding.invitationItemCourseLayout.invitationItemCourseDates.removeAllViews()

                course.weekDates?.forEach { date ->
                    val chip = Chip(context)
                    chip.text = date.toString()

                    binding.invitationItemCourseLayout.invitationItemCourseDates.addView(chip)
                }
            }

            if (meeting != null) {
                binding.invitationItemMeetingLayout.meeting = meeting

                binding.invitationItemMeetingLayout.invitationItemMeetingWeekDates.removeAllViews()

                meeting.weekDate?.let { weekDate ->
                    val chip = Chip(context)
                    chip.text = weekDate.toString()

                    binding.invitationItemMeetingLayout.invitationItemMeetingWeekDates.addView(chip)
                }
            }

            if (course != null || meeting != null) {

                var attachmentVisible = true

                val root = if (invitationItem.invitation.type == Invitation.Contract.TYPE_MEETING) {
                    binding.invitationItemAttachmentButton.text =
                        context.getString(R.string.invitation_attachment_meeting_text)
                    binding.invitationItemMeetingLayout.root
                } else {
                    binding.invitationItemAttachmentButton.text =
                        context.getString(R.string.invitation_attachment_course_text)
                    binding.invitationItemCourseLayout.root
                }

                binding.invitationItemAttachmentButton.setOnClickListener {
                    if (!attachmentVisible) {
                        binding.invitationItemAttachmentButton.icon =
                            AppCompatResources.getDrawable(context, R.drawable.ic_dropup_arrow_icon)
                        root.visibility = View.VISIBLE

                    } else {
                        binding.invitationItemAttachmentButton.icon =
                            AppCompatResources.getDrawable(context, R.drawable.ic_dropdown_arrow_icon)
                        root.visibility = View.GONE
                    }

                    attachmentVisible = !attachmentVisible
                }
            } else {
                binding.invitationItemAttachmentButton.visibility = View.GONE
            }
        }
    }

    sealed class Item {
        abstract val id: String

        data class Header(val titleId: Int): Item() {
            override val id = titleId.toString()

            companion object {
                const val ID = 0
            }
        }

        data class Invitation(
            val invitation: com.piotrokninski.teacherassistant.model.Invitation,
            val received: Boolean
        ): Item() {
            override val id = invitation.id ?: invitation.invitingUserId

            fun getInvitationDescription(): String? {

                val friendStarter = if (received) {
                    "Zaprasza cię"
                } else {
                    "Zapraszony"
                }

                val eventStarter = if (received) {
                    "Zaprasza"
                } else {
                    "Zapraszony"
                }

                return when (invitation.type) {
                    com.piotrokninski.teacherassistant.model.Invitation.Contract.TYPE_FRIENDSHIP -> {
                        when (invitation.invitedAs) {
                            com.piotrokninski.teacherassistant.model.Invitation.Contract.STUDENT ->
                                "$friendStarter do grona uczniów"

                            com.piotrokninski.teacherassistant.model.Invitation.Contract.TUTOR -> {
                                if (received) {
                                    "Zaprasza cię jako swojego korepetytora"
                                } else {
                                    "Zaproszony jako korepetytor"
                                }
                            }

                            com.piotrokninski.teacherassistant.model.Invitation.Contract.FRIEND ->
                                "$friendStarter do grona znajomych"

                            else -> null
                        }
                    }

                    com.piotrokninski.teacherassistant.model.Invitation.Contract.TYPE_COURSE ->
                        "$eventStarter na nowe zajęcia"

                    com.piotrokninski.teacherassistant.model.Invitation.Contract.TYPE_MEETING ->
                        "$eventStarter na nowe spotkanie"

                    else -> null
                }
            }

            companion object {
                const val ID = 1
            }
        }

        data class Homework(val homework: com.piotrokninski.teacherassistant.model.course.Homework): Item() {
            override val id = homework.toString()

            companion object {
                const val ID = 2
            }
        }
    }
}