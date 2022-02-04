package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.NoteListItemBinding
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.util.AppConstants

class NotesAdapter(
    private val clickListener: (Lesson) -> Unit,
    private val viewType: String,
    private val context: Context
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val notes = ArrayList<Lesson>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = NoteListItemBinding.inflate(layoutInflater, parent, false)

        return NoteViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position], clickListener, viewType)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(lessons: List<Lesson>) {
        this.notes.clear()
        this.notes.addAll(lessons)
        notifyDataSetChanged()
    }

    class NoteViewHolder(
        private val binding: NoteListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            lesson: Lesson,
            clickListener: (Lesson) -> Unit,
            viewType: String
        ) {
            binding.note = lesson
            binding.noteItemLayout.setOnClickListener { clickListener(lesson) }

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {
                    binding.noteItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.tutor_title_text)
                    )
                    binding.noteItemUserFullName.text = lesson.tutorFullName
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.noteItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.student_title_text)
                    )
                    binding.noteItemUserFullName.text = lesson.studentFullName
                }
            }
        }

    }
}