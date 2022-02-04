package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.databinding.NewLessonDialogBinding

class NewLessonDialogFragment(private val callback: (name: String, date: String, note: String) -> Unit,
                              private val subject: String): DialogFragment() {

    private lateinit var binding: NewLessonDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = NewLessonDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.noteDialogSubject.text = subject

        binding.noteDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.noteDialogConfirmButton.setOnClickListener {

            val name = binding.noteDialogName.text.toString()
            val date = binding.noteDialogDate.text.toString()
            val note = binding.noteDialogNote.text.toString()

            if (name.isEmpty() || date.isEmpty() || note.isEmpty()) {
                Toast.makeText(activity, "Uzupełnij notatkę", Toast.LENGTH_SHORT).show()
            } else {
                callback(name, date, note)
                dismiss()
            }
        }
    }
}