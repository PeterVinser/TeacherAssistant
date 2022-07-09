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
import com.piotrokninski.teacherassistant.databinding.*
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.util.AppConstants
import java.text.SimpleDateFormat
import kotlin.math.log

class HomeAdapter(
    private val itemClickListener: (HomeAdapterItem) -> Unit,
    private val positiveButtonListener: (HomeAdapterItem) -> Unit,
    private val negativeButtonListener: (HomeAdapterItem) -> Unit,
    private val viewType: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "HomeAdapter"

    private val items = ArrayList<HomeAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            HOME_HEADER_ITEM -> HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)

            HOME_INVITATION_ITEM -> InvitationViewHolder(
                InvitationViewHolder.initBinding(parent),
                context
            )

            HOME_HOMEWORK_ITEM -> HomeworkViewHolder(
                HomeworkViewHolder.initBinding(parent),
                context,
                this.viewType
            )

            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {

            HOME_HEADER_ITEM -> (holder as HeaderViewHolder).bind(
                items[position] as HomeAdapterItem.HeaderItem
            )

            HOME_INVITATION_ITEM -> (holder as InvitationViewHolder).bind(
                items[position] as HomeAdapterItem.InvitationItem,
                itemClickListener,
                positiveButtonListener,
                negativeButtonListener
            )

            HOME_HOMEWORK_ITEM -> (holder as HomeworkViewHolder).bind(
                items[position] as HomeAdapterItem.HomeworkItem,
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

            is HomeAdapterItem.HeaderItem -> HOME_HEADER_ITEM

            is HomeAdapterItem.InvitationItem -> HOME_INVITATION_ITEM

            is HomeAdapterItem.HomeworkItem -> HOME_HOMEWORK_ITEM
        }
    }

    fun setItems(items: List<HomeAdapterItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class HeaderViewHolder(
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

        fun bind(homeHeaderItem: HomeAdapterItem.HeaderItem) {
            binding.headerItemTitle.text = context.getString(homeHeaderItem.titleId)
        }
    }

    class InvitationViewHolder(
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
            invitationItem: HomeAdapterItem.InvitationItem,
            itemClickListener: (HomeAdapterItem) -> Unit,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
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

                meeting.weekDates?.forEach { weekDate ->
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

    class HomeworkViewHolder(
        private val binding: HomeHomeworkListItemBinding,
        private val context: Context,
        private val viewType: String
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HomeHomeworkListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HomeHomeworkListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            homeworkItem: HomeAdapterItem.HomeworkItem,
            itemClickListener: (HomeAdapterItem) -> Unit,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
        ) {
            val homework = homeworkItem.homework

            binding.homework = homework

            binding.homeHomeworkItemLayout.setOnClickListener { itemClickListener(homeworkItem) }

            val simpleFormat = SimpleDateFormat.getInstance()

            binding.homeHomeworkItemDueDate.text = context.getString(
                R.string.home_homework_due_date_text,
                simpleFormat.format(homework.dueDate!!)
            )

            if (viewType == AppConstants.VIEW_TYPE_STUDENT) {
                binding.homeHomeworkItemButtonsLayout.visibility = View.GONE
                binding.homeHomeworkItemDeleteButton.setOnClickListener {
                    negativeButtonListener(
                        homeworkItem
                    )
                }
                binding.homeHomeworkItemEditButton.setOnClickListener {
                    positiveButtonListener(
                        homeworkItem
                    )
                }
            }
        }
    }

    companion object {
        const val HOME_HEADER_ITEM = 0
        const val HOME_INVITATION_ITEM = 1
        const val HOME_HOMEWORK_ITEM = 2
    }
}