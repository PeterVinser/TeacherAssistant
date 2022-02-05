package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.HomeCourseListItemBinding
import com.piotrokninski.teacherassistant.databinding.HomeHomeworkListItemBinding
import com.piotrokninski.teacherassistant.databinding.HomeInvitationListItemBinding
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

            HOME_INVITATION_ITEM -> InvitationViewHolder(InvitationViewHolder.initBinding(parent))

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
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {

            is HomeAdapterItem.HeaderItem -> HOME_HEADER_ITEM

            is HomeAdapterItem.InvitationItem -> HOME_INVITATION_ITEM

            is HomeAdapterItem.CourseItem -> HOME_COURSE_ITEM

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

    class InvitationViewHolder(private val binding: HomeInvitationListItemBinding) :
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
            friendInvitationItem: HomeAdapterItem.InvitationItem,
            itemClickListener: (HomeAdapterItem) -> Unit,
            positiveButtonListener: (HomeAdapterItem) -> Unit,
            negativeButtonListener: (HomeAdapterItem) -> Unit
        ) {
            binding.friendInvitation = friendInvitationItem
            binding.homeFriendInvitation.text = friendInvitationItem.getInvitationType()

            binding.homeFriendItemLayout.setOnClickListener { itemClickListener(friendInvitationItem) }
            binding.homeInvitationItemRejectButton.setOnClickListener {
                negativeButtonListener(
                    friendInvitationItem
                )
            }
            binding.homeInvitationItemConfirmButton.setOnClickListener {
                positiveButtonListener(
                    friendInvitationItem
                )
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

            private const val TAG = "HomeAdapter"
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

            course.meetingsDates!!.forEach { date ->
                val chip = Chip(context)
                chip.text = date

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

    companion object {
        const val HOME_HEADER_ITEM = 0
        const val HOME_INVITATION_ITEM = 1
        const val HOME_COURSE_ITEM = 2
        const val HOME_HOMEWORK_ITEM = 3
    }
}