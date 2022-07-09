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
    private val context: Context
) :
    RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    private val courses = ArrayList<Course>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = CourseListItemBinding.inflate(layoutInflater, parent, false)

        return CourseViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position], clickListener)
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    fun setCourses(courses: List<Course>) {
        this.courses.clear()
        this.courses.addAll(courses)
        notifyDataSetChanged()
    }

    class CourseViewHolder(
        private val binding: CourseListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            course: Course,
            clickListener: (Course) -> Unit
        ) {
            binding.course = course
            binding.courseItemLayout.setOnClickListener { clickListener(course) }

            course.weekDates?.forEach { date ->
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