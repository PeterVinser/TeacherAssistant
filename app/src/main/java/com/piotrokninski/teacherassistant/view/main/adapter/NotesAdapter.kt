package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.NoteListItemBinding
import com.piotrokninski.teacherassistant.model.course.Note
import com.piotrokninski.teacherassistant.util.AppConstants

class NotesAdapter(
    private val clickListener: (Note) -> Unit,
    private val viewType: String,
    private val context: Context
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val notes = ArrayList<Note>()

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

    fun setNotes(notes: List<Note>) {
        this.notes.clear()
        this.notes.addAll(notes)
        notifyDataSetChanged()
    }

    class NoteViewHolder(
        private val binding: NoteListItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            note: Note,
            clickListener: (Note) -> Unit,
            viewType: String
        ) {
            binding.note = note
            binding.noteItemLayout.setOnClickListener { clickListener(note) }

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {
                    binding.noteItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.tutor_title_text)
                    )
                    binding.noteItemUserFullName.text = note.tutorFullName
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.noteItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.student_title_text)
                    )
                    binding.noteItemUserFullName.text = note.studentFullName
                }
            }
        }

    }
}