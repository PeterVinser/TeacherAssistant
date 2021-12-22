package com.piotrokninski.teacherassistant.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationDetailsBinding
import com.piotrokninski.teacherassistant.model.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.util.Util
import com.piotrokninski.teacherassistant.view.main.dialog.MeetingPickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.InvitationDetailsViewModel
import com.piotrokninski.teacherassistant.viewmodel.factory.InvitationDetailsViewModelFactory

class InvitationDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val TAG = "InvitationDetailsFragme"

    private lateinit var binding: FragmentInvitationDetailsBinding

    private lateinit var invitationDetailsViewModel: InvitationDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInvitationDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).isBottomNavVisible(false)

        binding.invitationDetailsCourseItem.root.visibility = View.GONE
        binding.invitationDetailsCourseHeader.visibility = View.GONE

        arguments?.let {
            val safeArgs = InvitationDetailsFragmentArgs.fromBundle(it)
            setupViewModel(safeArgs.friendInvitation)
        }

        initTypeRadio()

        binding.invitationDetailsAddCourseButton.setOnClickListener {
            binding.invitationDetailsCourseHeader.visibility = View.VISIBLE
            val type = binding.invitationDetailsCourseItem.courseItemTypeSpinner.selectedItem.toString()
            invitationDetailsViewModel.addCourse(type)
        }

        binding.invitationDetailsCourseItem.courseItemTypeSpinner.onItemSelectedListener = this

        binding.invitationDetailsCourseItem.courseItemAddMeetingButton.setOnClickListener {
            onAddMeetingButtonClicked()
        }

        binding.invitationDetailsCourseItem.courseItemRemoveButton.setOnClickListener {
            onRemoveCourseClicked()
        }

        binding.invitationDetailsCancelButton.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding.invitationDetailsSendButton.setOnClickListener {
            onSendClicked()
        }
    }

    private fun setupViewModel(friendInvitation: FriendInvitation) {
        val factory = InvitationDetailsViewModelFactory(friendInvitation)

        invitationDetailsViewModel =
            ViewModelProvider(this, factory).get(InvitationDetailsViewModel::class.java)

        binding.invitationViewModel = invitationDetailsViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.invitationDetailsCourseItem.invitationViewModel = invitationDetailsViewModel
        binding.invitationDetailsCourseItem.lifecycleOwner = viewLifecycleOwner

        observeCourse()
    }

    private fun observeCourse() {
        invitationDetailsViewModel.course.observe(viewLifecycleOwner, { course ->
            if (course == null) {
                binding.invitationDetailsCourseHeader.visibility = View.GONE
                binding.invitationDetailsCourseItem.root.visibility = View.GONE
                binding.invitationDetailsCourseItem.courseItemTypeSpinner.setSelection(0)
                binding.invitationDetailsCourseItem.courseItemSubjects.text = null
                binding.invitationDetailsCourseItem.courseItemChipGroup.removeAllViews()
            } else {
                binding.invitationDetailsCourseItem.root.visibility = View.VISIBLE
            }
        })
    }

    private fun initTypeRadio() {

        when (invitationDetailsViewModel.friendInvitation.value!!.invitationType) {
            FirestoreFriendInvitationContract.TYPE_STUDENT -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_student
            )

            FirestoreFriendInvitationContract.TYPE_TUTOR -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_tutor
            )
        }
    }

    private fun onSendClicked() {
        if (invitationDetailsViewModel.course.value != null
            && (invitationDetailsViewModel.course.value!!.subject == null || invitationDetailsViewModel.course.value!!.meetingsDates == null)) {
            Toast.makeText(activity, "Uzupełnij szczegóły bądź usuń zajęcia", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onSendClicked: ${invitationDetailsViewModel.course.value!!.subject}")
            if (invitationDetailsViewModel.course.value!!.meetingsDates == null) Log.d(TAG, "onSendClicked: no meeting time selected")
        } else {
            invitationDetailsViewModel.sendInvitation()
            findNavController(this).navigate(R.id.action_invitationDetails_to_home)
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (adapterView != null) {
            //TODO fix the way the course type is assigned
            invitationDetailsViewModel.setCourseType(adapterView.getItemAtPosition(position).toString())
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun onAddMeetingButtonClicked() {
        val dialog = MeetingPickerDialogFragment { weekDay: String, hour: Int, minute: Int ->
            saveMeetingTime(
                weekDay,
                hour,
                minute
            )
        }
        dialog.show(childFragmentManager, "meetingPicker")
    }

    private fun onRemoveCourseClicked() {
        invitationDetailsViewModel.removeCourse()
    }

    private fun saveMeetingTime(weekDay: String, hour: Int, minute: Int) {
        val chip = Chip(activity)
        val meetingTime = Util.formatMeetingTime(weekDay, hour, minute)
        chip.text = meetingTime
        binding.invitationDetailsCourseItem.courseItemChipGroup.addView(chip)

        invitationDetailsViewModel.addMeetingDate(meetingTime!!)
    }
}