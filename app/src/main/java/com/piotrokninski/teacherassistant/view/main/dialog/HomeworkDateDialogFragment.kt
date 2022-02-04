package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.databinding.HomeworkDateDialogBinding
import java.util.*

class HomeworkDateDialogFragment(
    private val callback: (date: Date) -> Unit
): DialogFragment() {
    private val TAG = "HomeworkDateDialogFragm"

    private lateinit var binding: HomeworkDateDialogBinding

    private var date: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeworkDateDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeworkDateDialogDatePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()

            calendar.set(year, month, dayOfMonth)

            date = calendar.time
        }

        binding.homeworkDateDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.homeworkDateDialogNextButton.setOnClickListener {
            if (date == null) {
                Toast.makeText(activity, "Wybierz datę", Toast.LENGTH_SHORT).show()
            } else {
                callback(date!!)
                dismiss()
            }
        }
    }
}