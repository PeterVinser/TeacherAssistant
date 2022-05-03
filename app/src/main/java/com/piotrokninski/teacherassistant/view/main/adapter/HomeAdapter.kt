package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.*
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.util.AppConstants
import java.text.SimpleDateFormat

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

            HOME_COURSE_ITEM -> CourseViewHolder(
                CourseViewHolder.initBinding(parent),
                context,
                this.viewType
            )

            HOME_HOMEWORK_ITEM -> HomeworkViewHolder(
                HomeworkViewHolder.initBinding(parent),
                context,
                this.viewType
            )

            HOME_MEETING_INVITATION_ITEM -> MeetingInvitationViewHolder(
                MeetingInvitationViewHolder.initBinding(parent),
                context
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
                items[position] as HomeAdapterItem.FriendInvitationItem,
                itemClickListener,
                positiveButtonListener,
                negativeButtonListener
            )

            HOME_COURSE_ITEM -> (holder as CourseViewHolder).bind(
                items[position] as HomeAdapterItem.CourseItem,
                positiveButtonListener,
                negativeButtonListener
            )

            HOME_HOMEWORK_ITEM -> (holder as HomeworkViewHolder).bind(
                items[position] as HomeAdapterItem.HomeworkItem,
                itemClickListener,
                positiveButtonListener,
                negativeButtonListener
            )

            HOME_MEETING_INVITATION_ITEM -> (holder as MeetingInvitationViewHolder).bind(
                items[position] as HomeAdapterItem.MeetingInvitationItem,
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

            is HomeAdapterItem.FriendInvitationItem -> HOME_INVITATION_ITEM

            is HomeAdapterItem.CourseItem -> HOME_COURSE_ITEM

            is HomeAdapterItem.HomeworkItem -> HOME_HOMEWORK_ITEM

            is HomeAdapterItem.MeetingInvitationItem -> HOME_MEETING_INVITATION_ITEM
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
        private val binding: HomeInvitationListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HomeInvitationListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HomeInvitationListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            friendFriendInvitationItem: HomeAdapterItem.FriendInvitationItem,
            itemClickListener: (HomeAdapterItem) -> Unit,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
        ) {
            binding.friendInvitation = friendFriendInvitationItem
            binding.homeInvitationDescription.text = friendFriendInvitationItem.getInvitationType()

            val course = friendFriendInvitationItem.friendInvitation.course

            if (course == null) {
                binding.homeInvitationItemCourseButton.visibility = View.GONE
            }

            binding.homeInvitationItemLayout.setOnClickListener {
                itemClickListener(
                    friendFriendInvitationItem
                )
            }
            binding.homeInvitationItemRejectButton.setOnClickListener {
                negativeButtonListener(
                    friendFriendInvitationItem
                )
            }
            binding.homeInvitationItemConfirmButton.setOnClickListener {
                positiveButtonListener(
                    friendFriendInvitationItem
                )
            }

            binding.homeInvitationItemCourseLayout.root.visibility = View.GONE
            binding.homeInvitationItemCourseLayout.course = course

            binding.homeInvitationItemCourseLayout.homeInvitationItemCourseDates.removeAllViews()

            course?.meetingDates?.forEach { date ->
                val chip = Chip(context)
                chip.text = date.toString()

                binding.homeInvitationItemCourseLayout.homeInvitationItemCourseDates.addView(chip)
            }

            var courseLayoutVisible = false

            binding.homeInvitationItemCourseButton.setOnClickListener {
                if (!courseLayoutVisible) {
                    binding.homeInvitationItemCourseButton.icon =
                        AppCompatResources.getDrawable(context, R.drawable.ic_dropup_arrow_icon)
                    binding.homeInvitationItemCourseLayout.root.visibility = View.VISIBLE
                } else {
                    binding.homeInvitationItemCourseButton.icon =
                        AppCompatResources.getDrawable(context, R.drawable.ic_dropdown_arrow_icon)
                    binding.homeInvitationItemCourseLayout.root.visibility = View.GONE
                }

                courseLayoutVisible = !courseLayoutVisible
            }
        }
    }

    class CourseViewHolder(
        private val binding: HomeCourseListItemBinding,
        private val context: Context,
        private val viewType: String
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HomeCourseListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HomeCourseListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }

        }

        fun bind(
            courseItem: HomeAdapterItem.CourseItem,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
        ) {
            val course = courseItem.course
            binding.course = course

            binding.homeCourseItemCancelRejectButton.setOnClickListener {
                negativeButtonListener(
                    courseItem
                )
            }
            binding.homeCourseItemEditConfirmButton.setOnClickListener {
                positiveButtonListener(
                    courseItem
                )
            }

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {
                    binding.homeCourseItemStudentTutorFullName.text = context.getString(
                        R.string.home_course_tutor_full_name_text,
                        course.tutorFullName
                    )

                    binding.homeCourseItemCancelRejectButton.text =
                        context.getString(R.string.course_item_reject_button_text)

                    binding.homeCourseItemEditConfirmButton.text =
                        context.getString(R.string.course_item_confirm_button_text)
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.homeCourseItemStudentTutorFullName.text = context.getString(
                        R.string.home_course_student_full_name_text,
                        course.studentFullName
                    )

                    binding.homeCourseItemCancelRejectButton.text =
                        context.getString(R.string.course_item_cancel_button_text)

                    binding.homeCourseItemEditConfirmButton.text =
                        context.getString(R.string.course_item_edit_button_text)
                }
            }

            binding.homeCourseItemMeetingDates.removeAllViews()

            course.meetingDates?.forEach { date ->
                val chip = Chip(context)
                chip.text = date.toString()

                binding.homeCourseItemMeetingDates.addView(chip)
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

    class MeetingInvitationViewHolder(
        private val binding: HomeMeetingInvitationListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): HomeMeetingInvitationListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HomeMeetingInvitationListItemBinding.inflate(layoutInflater, parent, false)
            }
        }

        fun bind(
            meetingInvitationItem: HomeAdapterItem.MeetingInvitationItem,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
        ) {
            binding.item = meetingInvitationItem

            binding.homeMeetingInvitationItemDescription.text = if (meetingInvitationItem.invited) {
                context.getString(
                    R.string.home_meeting_invitation_item_invited,
                    meetingInvitationItem.meetingInvitation.invitingUserFullName
                )
            } else {
                context.getString(
                    R.string.home_meeting_invitation_item_inviting,
                    meetingInvitationItem.meetingInvitation.invitedUserFullName
                )
            }

            binding.homeMeetingInvitationItemNegativeButton.setOnClickListener {
                negativeButtonListener(meetingInvitationItem)
            }

            binding.homeMeetingInvitationItemPositiveButton.setOnClickListener {
                positiveButtonListener(meetingInvitationItem)
            }
        }
    }

    companion object {
        const val HOME_HEADER_ITEM = 0
        const val HOME_INVITATION_ITEM = 1
        const val HOME_COURSE_ITEM = 2
        const val HOME_HOMEWORK_ITEM = 3
        const val HOME_MEETING_INVITATION_ITEM = 4
    }
}