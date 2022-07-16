package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.databinding.CourseListItemBinding
import com.piotrokninski.teacherassistant.model.course.Course

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
}