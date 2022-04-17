package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.databinding.WeekDatePickerDialogBinding
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.util.WeekDays

class WeekDatePickerDialogFragment(
    private val callback: (meetingDate: WeekDate) -> Unit): DialogFragment() {

    private lateinit var binding: WeekDatePickerDialogBinding

    private var weekDate: WeekDate = WeekDate.createCurrentWeekDate()

    private var createdWithWeekDate: Boolean = false

    constructor(weekDate: WeekDate, callback: (weekDate: WeekDate) -> Unit) : this(callback = callback) {
        this.weekDate = weekDate
        this.createdWithWeekDate = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = WeekDatePickerDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.weekDatePickerWeekDayPicker.check(weekDate.weekDay.buttonId)
        binding.weekDatePickerTimePicker.hour = weekDate.hour
        binding.weekDatePickerTimePicker.minute = weekDate.minute

        binding.weekDatePickerDate.text = weekDate.toLocalString(requireContext())

        if (createdWithWeekDate) {
            binding.weekDatePickerDurationHour.setText(weekDate.durationHours.toString())
            binding.weekDatePickerDurationMinute.setText(weekDate.durationMinutes.toString())
        }

        binding.weekDatePickerWeekDayPicker.addOnButtonCheckedListener { _, checkedId, _ ->
            val weekDay = WeekDays.values().firstOrNull { it.buttonId == checkedId }
            if (weekDay != null) {
                weekDate.updateWeekDay(weekDay)
            }

            binding.weekDatePickerDate.text = weekDate.toLocalString(requireContext())
        }

        binding.weekDatePickerTimePicker.setOnTimeChangedListener { _, hour, minute ->
            weekDate.hour = hour
            weekDate.minute = minute

            binding.weekDatePickerDate.text = weekDate.toLocalString(requireContext())
        }

        binding.weekDatePickerDurationHour.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                weekDate.durationHours = text.toString().toInt()
            }
        }

        binding.weekDatePickerDurationMinute.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                weekDate.durationMinutes = text.toString().toInt()
            }
        }


        binding.weekDatePickerCancelButton.setOnClickListener {
            dismiss()
        }

        binding.weekDatePickerConfirmButton.setOnClickListener {
            callback(weekDate)
            dismiss()
        }
    }
}