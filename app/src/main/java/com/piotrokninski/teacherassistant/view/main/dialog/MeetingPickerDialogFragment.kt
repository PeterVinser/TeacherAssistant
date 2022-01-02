package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.MeetingPickerDialogBinding
import com.piotrokninski.teacherassistant.util.Util
import com.piotrokninski.teacherassistant.util.WeekDays

class MeetingPickerDialogFragment(
    private val callback: (weekDay: String, hour: Int, minute: Int) -> Unit): DialogFragment() {

    private lateinit var binding: MeetingPickerDialogBinding

    private var weekDay: String? = null
    private var minute: Int? = null
    private var hour: Int? = null

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
            when (checkedId) {
                R.id.meeting_dialog_monday -> weekDay = WeekDays.MONDAY.toString()

                R.id.meeting_dialog_tuesday -> weekDay = WeekDays.TUESDAY.toString()

                R.id.meeting_dialog_wednesday -> weekDay = WeekDays.WEDNESDAY.toString()

                R.id.meeting_dialog_thursday -> weekDay = WeekDays.THURSDAY.toString()

                R.id.meeting_dialog_friday -> weekDay = WeekDays.FRIDAY.toString()

                R.id.meeting_dialog_saturday -> weekDay = WeekDays.SATURDAY.toString()

                R.id.meeting_dialog_sunday -> weekDay = WeekDays.SUNDAY.toString()
            }

            binding.meetingDialogTime.text = Util.formatMeetingTime(weekDay, hour, minute)
        }

        binding.meetingDialogTimePicker.setOnTimeChangedListener { _, i, i2 ->
            hour = i
            minute = i2

            binding.meetingDialogTime.text = Util.formatMeetingTime(weekDay, hour, minute)
        }

        binding.meetingDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.meetingDialogConfirmButton.setOnClickListener {
            if (minute == null || hour == null || weekDay == null) {
                Toast.makeText(activity, "Wybierz termin", Toast.LENGTH_SHORT).show()
            } else {
                callback(weekDay!!, hour!!, minute!!)
                dismiss()
            }
        }
    }
}