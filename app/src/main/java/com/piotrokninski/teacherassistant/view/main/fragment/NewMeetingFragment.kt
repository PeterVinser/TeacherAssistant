package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentNewMeetingBinding
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.DatePickerDialogFragment
import com.piotrokninski.teacherassistant.view.main.dialog.WeekDatePickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.NewMeetingFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.NewMeetingFragmentViewModelFactory
import java.util.*

class NewMeetingFragment : Fragment() {
    private val TAG = "NewMeetingFragment"

    private lateinit var binding: FragmentNewMeetingBinding

    private lateinit var newMeetingViewModel: NewMeetingFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewMeetingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        initMeetingInvitation()

        setupButtons()
    }

    private fun initMeetingInvitation() {
        arguments?.let {
            val safeArgs = NewMeetingFragmentArgs.fromBundle(it)
            val meetingInvitation = safeArgs.meetingInvitation

            setupViewModel(safeArgs.meetingInvitation, safeArgs.id)

            when (meetingInvitation?.mode) {
                MeetingInvitation.MEETING_TYPE_SINGULAR -> {
                    binding.newMeetingSingularToggleButton.isChecked = true
                }

                MeetingInvitation.MEETING_TYPE_RECURRING -> {
                    binding.newMeetingRecurringToggleButton.isChecked = true
                }
            }
        }
    }

    private fun setupViewModel(meetingInvitation: MeetingInvitation?, id: String?) {
        val factory = NewMeetingFragmentViewModelFactory(meetingInvitation, id)
        newMeetingViewModel =
            ViewModelProvider(this, factory)[NewMeetingFragmentViewModel::class.java]

        binding.newMeetingViewModel = newMeetingViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeFriendNames()

        observeMeetingHolder()
    }

    private fun observeFriendNames() {
        newMeetingViewModel.friendFullNames.observe(viewLifecycleOwner) { fullNames ->
            if (!fullNames.isNullOrEmpty()) {
                if (newMeetingViewModel.editing) {
                    binding.newMeetingFriendTextView.setText(
                        fullNames[0],
                        false
                    )
                    binding.newMeetingFriendMenu.isEnabled = false
                } else {
                    val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, fullNames)
                    binding.newMeetingFriendTextView.setAdapter(arrayAdapter)
                }
            }
        }
    }

    private fun observeMeetingHolder() {
        newMeetingViewModel.meetingInvitation.observe(viewLifecycleOwner) { meetingHolder ->
            if (meetingHolder != null) {
                binding.newMeetingDate.text =
                    newMeetingViewModel.meetingInvitation.value!!.dateToString()
            }
        }
    }

    private fun setupButtons() {

        binding.newMeetingTypeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onMeetingTypeToggleClicked(checkedId, isChecked)
        }

        binding.newMeetingFriendTextView.setOnItemClickListener { _, _, position, _ ->
            onFriendSelected(position)
        }

        binding.newMeetingDateButton.setOnClickListener {
            onDateButtonClicked()
        }

        binding.newMeetingCancelButton.setOnClickListener {
            onCancelClicked()
        }

        binding.newMeetingConfirmButton.setOnClickListener {
            onConfirmClicked()
        }
    }

    private fun onMeetingTypeToggleClicked(checkedId: Int, isChecked: Boolean) {
        if (isChecked) {
            when (checkedId) {
                R.id.new_meeting_singular_toggle_button -> {
                    newMeetingViewModel.meetingInvitation.value!!.mode =
                        MeetingInvitation.MEETING_TYPE_SINGULAR
                }

                R.id.new_meeting_recurring_toggle_button -> {
                    newMeetingViewModel.meetingInvitation.value!!.mode =
                        MeetingInvitation.MEETING_TYPE_RECURRING
                }
            }

            binding.newMeetingDate.text = newMeetingViewModel.meetingInvitation.value!!.dateToString()
        }
    }

    private fun onFriendSelected(position: Int) {
        newMeetingViewModel.onFriendSelected(position)
    }

    private fun onDateButtonClicked() {
        when (newMeetingViewModel.meetingInvitation.value!!.mode) {
            MeetingInvitation.MEETING_TYPE_SINGULAR -> {
                val dialog = DatePickerDialogFragment(
                    newMeetingViewModel.meetingInvitation.value?.date,
                    newMeetingViewModel.meetingInvitation.value?.durationHours,
                    newMeetingViewModel.meetingInvitation.value?.durationMinutes,
                    DatePickerDialogFragment.MEETING_DATE_MODE
                ) { date, durationHours, durationMinutes ->
                    onSingularDateSelected(date, durationHours!!, durationMinutes!!)
                }
                dialog.show(childFragmentManager, "tag")
            }

            MeetingInvitation.MEETING_TYPE_RECURRING -> {
                val prevWeekDate = newMeetingViewModel.meetingInvitation.value?.weekDate

                val dialog = prevWeekDate?.let {
                    WeekDatePickerDialogFragment(it) { weekDate ->
                        onRecurringDateSelected(weekDate)
                    }
                } ?: WeekDatePickerDialogFragment { weekDate ->
                    onRecurringDateSelected(weekDate)
                }

                dialog.show(childFragmentManager, "tag")
            }
        }
    }

    private fun onSingularDateSelected(date: Date, durationHours: Int, durationMinutes: Int) {
        newMeetingViewModel.meetingInvitation.value!!.date = date
        newMeetingViewModel.meetingInvitation.value!!.durationHours = durationHours
        newMeetingViewModel.meetingInvitation.value!!.durationMinutes = durationMinutes
        binding.newMeetingDate.text = newMeetingViewModel.meetingInvitation.value!!.dateToString()
    }

    private fun onRecurringDateSelected(weekDate: WeekDate) {
        newMeetingViewModel.meetingInvitation.value!!.weekDate = weekDate
        binding.newMeetingDate.text = newMeetingViewModel.meetingInvitation.value!!.dateToString()
    }

    private fun onCancelClicked() {
        (activity as MainActivity).onBackPressed()
    }

    private fun onConfirmClicked() {
        val meetingAdded = newMeetingViewModel.addMeeting()

        if (!meetingAdded) {
            Toast.makeText(activity, "Uzupełnij dane spotkania", Toast.LENGTH_SHORT).show()
        } else {
            (activity as MainActivity).onBackPressed()
        }
    }
}