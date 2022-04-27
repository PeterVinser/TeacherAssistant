package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.CourseListItemBinding
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.CourseAdapterItem
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.util.AppConstants

class CoursesAdapter(
    private val clickListener: (Course) -> Unit,
    private val buttonClickListener: (Int, Course) -> Unit,
    private val viewType: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val courseItems = ArrayList<CourseAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COURSE_ITEM -> {
                CourseViewHolder(CourseViewHolder.initBinding(parent), context)
            }

            HEADER_ITEM -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)
            }

            else -> throw ClassCastException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            COURSE_ITEM -> {
                (holder as CourseViewHolder).bind(
                    (courseItems[position] as CourseAdapterItem.CourseItem).course,
                    clickListener,
                    buttonClickListener,
                    viewType
                )
            }

            HEADER_ITEM -> {
                (holder as HeaderViewHolder).bind(
                    courseItems[position] as CourseAdapterItem.HeaderItem
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return courseItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (courseItems[position]) {
            is CourseAdapterItem.CourseItem -> COURSE_ITEM
            is CourseAdapterItem.HeaderItem -> HEADER_ITEM
        }
    }

    fun setCourses(courseItems: List<CourseAdapterItem>) {
        this.courseItems.clear()
        this.courseItems.addAll(courseItems)
        notifyDataSetChanged()
    }

    class CourseViewHolder(
        private val binding: CourseListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): CourseListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return CourseListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            course: Course,
            clickListener: (Course) -> Unit,
            buttonClickListener: (Int, Course) -> Unit,
            viewType: String
        ) {
            binding.course = course
            binding.courseItemLayout.setOnClickListener { clickListener(course) }

            if (course.status == Course.STATUS_APPROVED) {
                binding.courseItemButtonsLayout.visibility = View.GONE
            }

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {
                    binding.courseItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.tutor_title_text)
                    )
                    binding.courseItemFullName.text = course.tutorFullName

                    binding.courseItemCancelRejectButton.text = context.getString(R.string.course_item_reject_button_text)
                    binding.courseItemCancelRejectButton.setOnClickListener { buttonClickListener(REJECT_BUTTON_ID, course) }

                    binding.courseItemEditConfirmButton.text = context.getString(R.string.course_item_confirm_button_text)
                    binding.courseItemEditConfirmButton.setOnClickListener { buttonClickListener(CONFIRM_BUTTON_ID, course) }
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.courseItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.student_title_text)
                    )
                    binding.courseItemFullName.text = course.studentFullName

                    binding.courseItemCancelRejectButton.text = context.getString(R.string.course_item_cancel_button_text)
                    binding.courseItemCancelRejectButton.setOnClickListener { buttonClickListener(CANCEL_BUTTON_ID, course) }

                    binding.courseItemEditConfirmButton.text = context.getText(R.string.course_item_edit_button_text)
                    binding.courseItemEditConfirmButton.setOnClickListener { buttonClickListener(EDIT_BUTTON_ID, course) }
                }
            }

            course.meetingDates!!.forEach { date ->
                val chip = Chip(context)
                chip.text = date.toString()

                binding.courseItemChipGroup.addView(chip)
            }
        }
    }

    class HeaderViewHolder(
        private val binding: HeaderListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
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

        fun bind(headerAdapterItem: CourseAdapterItem.HeaderItem) {
            binding.headerItemTitle.text = context.getString(headerAdapterItem.titleId)
            binding.headerItemDivider.visibility = View.GONE
        }
    }

    companion object {
        const val COURSE_ITEM = 1
        const val HEADER_ITEM = 2

        const val CANCEL_BUTTON_ID = 1
        const val REJECT_BUTTON_ID = 2
        const val EDIT_BUTTON_ID = 3
        const val CONFIRM_BUTTON_ID = 4
    }
}