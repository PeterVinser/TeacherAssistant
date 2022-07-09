package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationBinding
import com.piotrokninski.teacherassistant.databinding.InvitationCourseItemBinding
import com.piotrokninski.teacherassistant.databinding.InvitationMeetingItemBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.DatePickerDialogFragment
import com.piotrokninski.teacherassistant.view.main.dialog.WeekDatePickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.InvitationFragmentViewModelFactory
import java.util.*

class InvitationFragment : Fragment() {
    private val TAG = "InvitationFragme"

    private lateinit var binding: FragmentInvitationBinding

    private lateinit var invitationViewModel: InvitationFragmentViewModel

    private lateinit var mode: String
    private var meetingSingular: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInvitationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).isBottomNavVisible(false)

        arguments?.let {
            val safeArgs = InvitationFragmentArgs.fromBundle(it)
            val invitation = safeArgs.invitation

            val type = safeArgs.type

            mode = when (type) {
                Invitation.Contract.TYPE_FRIENDSHIP -> MODE_FRIENDSHIP
                Invitation.Contract.TYPE_COURSE -> MODE_COURSE
                Invitation.Contract.TYPE_MEETING -> MODE_MEETING
                else -> MODE_BLANK
            }

            setupViewModel(type, invitation)
        }


        binding.invitationCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.invitationSendButton.setOnClickListener {
            onSendClicked()
        }
    }

    private fun setupInvitation() {
        when (mode) {
            MODE_FRIENDSHIP -> initFriendInvitation()

            MODE_COURSE -> initCourseInvitation()

            MODE_MEETING -> initMeetingInvitation()
        }
    }

    private fun initFriendInvitation() {
        binding.invitationFriendMenu.visibility = View.GONE

        binding.invitationAttachmentTitle.visibility = View.GONE

        binding.invitationAttachmentTitle.text =
            getString(R.string.invitation_attachment_course_text)

        binding.invitationAttachment.viewStub?.layoutResource =
            R.layout.invitation_course_item

        binding.invitationAttachment.viewStub?.inflate()

        val courseBinding =
            binding.invitationAttachment.binding as InvitationCourseItemBinding

        courseBinding.invitationCourseItemAddMeetingButton.setOnClickListener {
            onAddMeetingButtonClicked()
        }

        courseBinding.invitationCourseItemRemoveButton.setOnClickListener {
            onRemoveCourseClicked()
        }

        courseBinding.invitationViewModel = invitationViewModel
        courseBinding.lifecycleOwner = viewLifecycleOwner

        setupCourseTypesMenu(courseBinding)

        binding.invitationAddCourseButton.setOnClickListener {
            invitationViewModel.addCourse()
        }
    }

    private fun initCourseInvitation() {
        binding.invitationFullName.visibility = View.GONE
        binding.invitationAddCourseButton.visibility = View.GONE

        binding.invitationAttachmentTitle.text =
            getString(R.string.invitation_attachment_course_text)

        binding.invitationAttachment.viewStub?.layoutResource =
            R.layout.invitation_course_item

        binding.invitationAttachment.viewStub?.inflate()

        val courseBinding =
            binding.invitationAttachment.binding as InvitationCourseItemBinding

        courseBinding.invitationCourseItemAddMeetingButton.setOnClickListener {
            onAddMeetingButtonClicked()
        }

        setupCourseTypesMenu(courseBinding)

        courseBinding.invitationViewModel = invitationViewModel
        courseBinding.lifecycleOwner = viewLifecycleOwner

        courseBinding.invitationCourseItemRemoveButton.visibility = View.GONE
    }

    private fun initMeetingInvitation() {

        binding.invitationFullName.visibility = View.GONE
        binding.invitationAddCourseButton.visibility = View.GONE

        binding.invitationAttachmentTitle.text =
            getString(R.string.invitation_attachment_meeting_text)

        binding.invitationAttachment.viewStub?.layoutResource =
            R.layout.invitation_meeting_item

        meetingSingular = true

        binding.invitationAttachment.viewStub?.inflate()

        val meetingBinding =
            binding.invitationAttachment.binding as InvitationMeetingItemBinding

        meetingBinding.invitationViewModel = invitationViewModel
        meetingBinding.lifecycleOwner = viewLifecycleOwner

        meetingBinding.invitationMeetingSingularToggleButton.toggle()
        meetingBinding.invitationMeetingTypeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            onMeetingTypeToggleClicked(checkedId, isChecked)
        }

        meetingBinding.invitationMeetingDateButton.setOnClickListener {
            onDateButtonClicked()
        }
    }

    private fun onMeetingTypeToggleClicked(
        checkedId: Int,
        isChecked: Boolean
    ) {
        if (isChecked) {
            when (checkedId) {
                R.id.invitation_meeting_singular_toggle_button -> {
                    invitationViewModel.updateMeetingType(true)
                    meetingSingular = true
                }

                R.id.invitation_meeting_recurring_toggle_button -> {
                    invitationViewModel.updateMeetingType(false)
                    meetingSingular = false
                }
            }
        }
    }


    private fun onDateButtonClicked() {
        if (meetingSingular != null) {
            if (meetingSingular == true) {

                val dialog = DatePickerDialogFragment(
                    invitationViewModel.meeting.value?.date,
                    invitationViewModel.meeting.value?.durationHours,
                    invitationViewModel.meeting.value?.durationMinutes,
                    DatePickerDialogFragment.MEETING_DATE_MODE
                ) { date, durationHours, durationMinutes ->
                    onSingularDateSelected(date, durationHours!!, durationMinutes!!)
                }

                dialog.show(childFragmentManager, "tag")
            } else {
                val prevWeekDate = invitationViewModel.meeting.value?.weekDates?.get(0)

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
        invitationViewModel.onSingularDateSelected(date, durationHours, durationMinutes)
    }

    private fun onRecurringDateSelected(weekDate: WeekDate) {
        invitationViewModel.onRecurringDateSelected(weekDate)
    }

    private fun setupCourseTypesMenu(courseBinding: InvitationCourseItemBinding) {
        val types = resources.getStringArray(R.array.lesson_types)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        courseBinding.invitationCourseTypeTextView.setAdapter(arrayAdapter)

        courseBinding.invitationCourseTypeTextView.setOnItemClickListener { adapterView, _, position, _ ->
            onTypeSelected(adapterView, position)
        }
    }

    private fun onTypeSelected(adapterView: AdapterView<*>, position: Int) {
        invitationViewModel.setCourseType(adapterView.getItemAtPosition(position).toString())
    }

    private fun onFriendSelected(position: Int) {
        invitationViewModel.setSelectedFriend(position)
    }

    private fun setupViewModel(type: String, invitation: Invitation?) {
        val factory = InvitationFragmentViewModelFactory(type, invitation)

        invitationViewModel =
            ViewModelProvider(this, factory)[InvitationFragmentViewModel::class.java]

        binding.invitationViewModel = invitationViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupInvitation()

        observeCourse()
        observeMeeting()
        observeFriends()
    }

    private fun observeCourse() {
        invitationViewModel.course.observe(viewLifecycleOwner) { course ->
            if (course == null && mode == MODE_FRIENDSHIP) {
                binding.invitationAddCourseButton.visibility = View.VISIBLE
                binding.invitationAttachmentTitle.visibility = View.GONE

                binding.invitationAttachment.root?.visibility = View.GONE
            } else if (course != null) {
                binding.invitationAddCourseButton.visibility = View.GONE

                binding.invitationAttachmentTitle.visibility = View.VISIBLE

                binding.invitationAttachment.root?.visibility = View.VISIBLE

                (binding.invitationAttachment.binding as InvitationCourseItemBinding)
                    .invitationCourseTypeTextView.setText(course!!.type, false)

                course.weekDates?.forEach { meetingDate ->
                    val chip = Chip(activity)
                    chip.text = meetingDate.toString()
                    (binding.invitationAttachment.binding as InvitationCourseItemBinding)
                        .invitationCourseItemChipGroup.addView(chip)
                }
            }
        }
    }

    private fun observeMeeting() {
        invitationViewModel.meeting.observe(viewLifecycleOwner) { meeting ->
            if (meeting != null) {
                Log.d(TAG, "observeMeeting: $meeting")
                meetingSingular = meeting.singular

                val meetingBinding = binding.invitationAttachment.binding as InvitationMeetingItemBinding

                if (meetingSingular!!) {
                    meetingBinding.invitationMeetingSingularToggleButton.toggle()
                } else {
                    meetingBinding.invitationMeetingRecurringToggleButton.toggle()
                }
            }
        }
    }

    private fun observeFriends() {
        invitationViewModel.friendFullNames.observe(viewLifecycleOwner) { fullNames ->
            if (!fullNames.isNullOrEmpty() && (mode == MODE_COURSE || mode == MODE_MEETING)) {
                if (invitationViewModel.editing) {
                    binding.invitationMenuTextView.setText(
                        fullNames[0],
                        false
                    )
                    binding.invitationFriendMenu.isEnabled = false
                } else {
                    val arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item, fullNames)
                    binding.invitationMenuTextView.setAdapter(arrayAdapter)

                    binding.invitationMenuTextView.setOnItemClickListener { _, _, position, _ ->
                        onFriendSelected(position)
                    }
                }
            }
        }
    }

    private fun onSendClicked() {
        if (invitationViewModel.sendInvitation()) {
            this.findNavController().navigate(R.id.action_invitation_to_home)
        } else {
            Toast.makeText(activity, "Uzupełnij szczegóły bądź usuń zajęcia", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onAddMeetingButtonClicked() {
        val dialog = WeekDatePickerDialogFragment { meetingDate: WeekDate ->
            saveCourseWeekDate(meetingDate)
        }
        dialog.show(childFragmentManager, "meetingPicker")
    }

    private fun onRemoveCourseClicked() {
        invitationViewModel.removeCourse()
    }

    private fun saveCourseWeekDate(meetingDate: WeekDate) {
        val chip = Chip(activity)
        chip.text = meetingDate.toString()
        (binding.invitationAttachment.binding as InvitationCourseItemBinding)
            .invitationCourseItemChipGroup.addView(chip)

        invitationViewModel.addMeetingDate(meetingDate)
    }

    companion object {
        const val MODE_FRIENDSHIP = "friendship"
        const val MODE_COURSE = "course"
        const val MODE_MEETING = "meeting"
        const val MODE_BLANK = "blank"
    }
}