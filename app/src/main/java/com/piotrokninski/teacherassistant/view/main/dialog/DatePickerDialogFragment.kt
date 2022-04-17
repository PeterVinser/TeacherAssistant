package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.DatePickerDialogBinding
import java.util.*

class DatePickerDialogFragment(
    private var date: Date?,
    private var durationHours: Int?,
    private var durationMinutes: Int?,
    private val mode: String,
    private val callback: (date: Date, durationHours: Int?, durationMinutes: Int?) -> Unit
) : DialogFragment() {
    private val TAG = "DatePickerDialogFragmen"

    private lateinit var binding: DatePickerDialogBinding

    private var hour: Int = 12
    private var minute: Int = 0

    private val calendar = Calendar.getInstance()

    init {

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (date == null) {
            date = calendar.time
        } else {
            calendar.time = date!!
        }

        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DatePickerDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.datePickerCalendar.date = calendar.timeInMillis


        when (mode) {
            DATE_MODE -> {
                binding.datePickerTypeToggleGroup.visibility = View.GONE
                binding.datePickerTime.visibility = View.GONE
                binding.datePickerDurationLayout.visibility = View.GONE
                binding.datePickerSelectedDate.visibility = View.GONE

                binding.datePickerTitle.text = getString(R.string.date_picker_date_title)
            }

            MEETING_DATE_MODE -> {
                binding.datePickerTime.visibility = View.GONE
                binding.datePickerDateToggleButton.isChecked = true

                binding.datePickerTitle.text = getString(R.string.date_picker_meeting_title)

                binding.datePickerSelectedDate.text = calendar.time.toLocaleString()

                binding.datePickerTime.hour = hour
                binding.datePickerTime.minute = minute

                if (durationHours != null && durationMinutes != null) {
                    binding.datePickerDurationHour.setText(durationHours.toString())
                    binding.datePickerDurationMinute.setText(durationMinutes.toString())
                    Log.d(TAG, "onViewCreated: durations updated")
                } else {
                    durationHours = 1
                    durationMinutes = 0
                }
            }
        }

        binding.datePickerTypeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.date_picker_date_toggle_button -> {
                        binding.datePickerCalendar.visibility = View.VISIBLE
                        binding.datePickerTime.visibility = View.GONE
                    }

                    R.id.date_picker_time_toggle_button -> {
                        binding.datePickerTime.visibility = View.VISIBLE
                        binding.datePickerCalendar.visibility = View.GONE
                    }
                }
            }
        }

        binding.datePickerCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->

            calendar.set(year, month, dayOfMonth, hour, minute, 0)

            date = calendar.time

            if (mode == MEETING_DATE_MODE) {
                binding.datePickerSelectedDate.text = date!!.toLocaleString()
                binding.datePickerTypeToggleGroup.check(R.id.date_picker_time_toggle_button)
            }
        }

        binding.datePickerTime.setOnTimeChangedListener { _, hour, minute ->
            this.hour = hour
            this.minute = minute

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            date = calendar.time

            binding.datePickerSelectedDate.text = date!!.toLocaleString()
        }

        binding.datePickerDurationHour.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                durationHours = text.toString().toInt()
            }
        }

        binding.datePickerDurationMinute.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                durationMinutes = text.toString().toInt()
            }
        }

        binding.datePickerCancelButton.setOnClickListener {
            dismiss()
        }

        binding.datePickerConfirmButton.setOnClickListener {
            when (mode) {
                DATE_MODE -> {
                    callback(date!!, null, null)
                    dismiss()
                }

                MEETING_DATE_MODE -> {
                    callback(date!!, durationHours, durationMinutes)
                    dismiss()
                }
            }
        }
    }

    companion object {
        const val DATE_MODE = "dateMode"
        const val MEETING_DATE_MODE = "meetingDateMode"
    }
}