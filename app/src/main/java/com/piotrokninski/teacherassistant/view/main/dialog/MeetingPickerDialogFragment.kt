package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.MeetingPickerDialogBinding
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.util.WeekDays

class MeetingPickerDialogFragment(
    private val callback: (meetingDate: WeekDate) -> Unit): DialogFragment() {

    private lateinit var binding: MeetingPickerDialogBinding

    private val weekDate = WeekDate()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MeetingPickerDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.meetingDialogWeekDayPicker.addOnButtonCheckedListener { _, checkedId, _ ->
            var weekDay = when (checkedId) {
                R.id.meeting_dialog_monday -> WeekDays.MONDAY

                R.id.meeting_dialog_tuesday -> WeekDays.TUESDAY

                R.id.meeting_dialog_wednesday -> WeekDays.WEDNESDAY

                R.id.meeting_dialog_thursday -> WeekDays.THURSDAY

                R.id.meeting_dialog_friday -> WeekDays.FRIDAY

                R.id.meeting_dialog_saturday -> WeekDays.SATURDAY

                R.id.meeting_dialog_sunday -> WeekDays.SUNDAY

                else -> throw IllegalArgumentException("Id not found")
            }

            weekDate.updateWeekDay(weekDay)

            binding.meetingDialogTime.text = weekDate.toString()
        }

        binding.meetingDialogTimePicker.setOnTimeChangedListener { _, hour, minute ->
            weekDate.hour = hour
            weekDate.minute = minute

            binding.meetingDialogTime.text = weekDate.toString()
        }

        binding.meetingDialogDurationHour.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                weekDate.durationHours = text.toString().toInt()
            }
        }

        binding.meetingDialogDurationMinute.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                weekDate.durationMinutes = text.toString().toInt()
            }
        }


        binding.meetingDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.meetingDialogConfirmButton.setOnClickListener {
            if (!weekDate.isComplete()) {
                Toast.makeText(activity, "Wybierz termin", Toast.LENGTH_SHORT).show()
            } else {
                callback(weekDate)
                dismiss()
            }
        }
    }
}