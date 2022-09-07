package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationDetailsBinding
import com.piotrokninski.teacherassistant.databinding.InvitationCourseItemBinding
import com.piotrokninski.teacherassistant.databinding.InvitationMeetingItemBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.DatePickerDialogFragment
import com.piotrokninski.teacherassistant.view.main.dialog.WeekDatePickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationDetailsViewModel
import java.util.*

class InvitationDetailsFragment : Fragment() {
    private val TAG = "InvitationFragme"

    private lateinit var binding: FragmentInvitationDetailsBinding

    private lateinit var invitationViewModel: InvitationDetailsViewModel

    private var mode: String = MODE_BLANK
    private var editable: Boolean = true
    private var meetingSingular: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInvitationDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).isBottomNavVisible(false)

        arguments?.let {
            val safeArgs = InvitationDetailsFragmentArgs.fromBundle(it)

            val invitation = safeArgs.invitation
            val invitationId = safeArgs.invitationId
            val type = safeArgs.type

            val editable = safeArgs.editable
            binding.editable = editable
            this.editable = editable

            mode = if (type != null) {
                MODE_NEW
            } else {
                if (editable) {
                    MODE_EDITING
                } else {
                    MODE_OVERVIEW
                }
            }

            binding.invitationSendButton.text = when (mode) {
                MODE_NEW -> getString(R.string.invitation_send_text)
                MODE_EDITING -> getString(R.string.edit_button_text)
                MODE_OVERVIEW -> getString(R.string.confirm_button_text)
                else -> null
            }

            setupViewModel(type, invitation, invitationId)
        }


        binding.invitationCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.invitationSendButton.setOnClickListener {
            onPositiveButtonClicked()
        }
    }

    private fun setupViewModel(type: String?, invitation: Invitation?, invitationId: String?) {
        val factory = InvitationDetailsViewModel.Factory(type, invitation, invitationId)

        invitationViewModel =
            ViewModelProvider(this, factory)[InvitationDetailsViewModel::class.java]

        binding.invitationViewModel = invitationViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (type != null) {
            initInvitation(type)
        }

        observeInvitation()
        observeFriends()
    }

    private fun observeInvitation() {
        invitationViewModel.invitation.observe(viewLifecycleOwner) { invitation ->
            if (invitation != null) {

                initInvitation(invitation.type)

                if (
                    mode == MODE_OVERVIEW &&
                    invitation.invitedUserId != FirebaseAuth.getInstance().currentUser?.uid
                ) {
                    binding.invitationSendButton.visibility = View.GONE
                }

                if (
                    invitation.course != null &&
                    (invitation.type == Invitation.Contract.TYPE_COURSE || invitation.type == Invitation.Contract.TYPE_FRIENDSHIP)
                ) {
                    setupCourse(invitation.course!!)
                }

                if (invitation.meeting != null && invitation.type == Invitation.Contract.TYPE_MEETING) {
                    setupMeeting(invitation.meeting!!)
                }
            }
        }
    }

    private fun setupCourse(course: Course) {
        binding.invitationAddCourseButton.visibility = View.GONE

        binding.invitationAttachmentTitle.visibility = View.VISIBLE

        binding.invitationAttachment.root?.visibility = View.VISIBLE

        (binding.invitationAttachment.binding as InvitationCourseItemBinding)
            .invitationCourseTypeTextView.setText(course.type, false)

        course.weekDates?.forEach { meetingDate ->
            val chip = Chip(activity)
            chip.text = meetingDate.toString()
            (binding.invitationAttachment.binding as InvitationCourseItemBinding)
                .invitationCourseItemChipGroup.addView(chip)
        }
    }

    private fun setupMeeting(meeting: Meeting) {
        meetingSingular = meeting.singular

        val meetingBinding = binding.invitationAttachment.binding as InvitationMeetingItemBinding

        if (meetingSingular!!) {
            meetingBinding.invitationMeetingSingularToggleButton.toggle()
        } else {
            meetingBinding.invitationMeetingRecurringToggleButton.toggle()
        }
    }

    private fun observeFriends() {
        invitationViewModel.friendFullNames.observe(viewLifecycleOwner) { fullNames ->
            if (!fullNames.isNullOrEmpty()) {
                if (mode != MODE_NEW) {
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

    private fun initInvitation(type: String?) {
        when (type) {
            Invitation.Contract.TYPE_FRIENDSHIP -> initFriendInvitation()
            Invitation.Contract.TYPE_COURSE -> initCourseInvitation()
            Invitation.Contract.TYPE_MEETING -> initMeetingInvitation()
        }
    }

    private fun initFriendInvitation() {
        binding.invitationFriendMenu.visibility = View.GONE

        binding.invitationAttachmentTitle.visibility = View.GONE

        binding.invitationAddCourseButton.visibility = View.VISIBLE

        binding.invitationAttachment.root?.visibility = View.GONE

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
        courseBinding.editable = editable
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
        courseBinding.editable = editable
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
        meetingBinding.editable = editable
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
                val prevWeekDate = invitationViewModel.meeting.value?.weekDate

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

    private fun onPositiveButtonClicked() {
        val passed = when (mode) {
            MODE_NEW -> invitationViewModel.sendInvitation()
            MODE_EDITING -> invitationViewModel.updateInvitation()
            MODE_OVERVIEW -> invitationViewModel.confirmInvitation()
            else -> false
        }

        if (passed) {
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
        const val MODE_NEW = "new"
        const val MODE_EDITING = "editing"
        const val MODE_OVERVIEW = "overview"
        const val MODE_BLANK = "blank"
    }
}