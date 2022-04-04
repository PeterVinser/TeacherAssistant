package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.databinding.DatePickerDialogBinding
import java.util.*

class DatePickerDialogFragment(private var date: Date, private val callback: (date: Date) -> Unit): DialogFragment() {
    private lateinit var binding: DatePickerDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DatePickerDialogBinding.inflate(inflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePickerCalendar.date = date.time

        binding.datePickerCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()

            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            date = calendar.time
        }

        binding.datePickerCancelButton.setOnClickListener {
            dismiss()
        }

        binding.datePickerConfirmButton.setOnClickListener {
            callback(date)
            dismiss()
        }
    }
}