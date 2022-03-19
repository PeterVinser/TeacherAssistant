package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationDetailsBinding
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.util.Util
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.MeetingPickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationDetailsFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.InvitationDetailsFragmentViewModelFactory

class InvitationDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val TAG = "InvitationDetailsFragme"

    private lateinit var binding: FragmentInvitationDetailsBinding

    private lateinit var invitationDetailsFragmentViewModel: InvitationDetailsFragmentViewModel

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
            val type =
                binding.invitationDetailsCourseItem.invitationCourseItemTypeSpinner.selectedItem.toString()
            invitationDetailsFragmentViewModel.addCourse(type)
        }

        binding.invitationDetailsCourseItem.invitationCourseItemTypeSpinner.onItemSelectedListener =
            this

        binding.invitationDetailsCourseItem.invitationCourseItemAddMeetingButton.setOnClickListener {
            onAddMeetingButtonClicked()
        }

        binding.invitationDetailsCourseItem.invitationCourseItemRemoveButton.setOnClickListener {
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
        val factory = InvitationDetailsFragmentViewModelFactory(friendInvitation)

        invitationDetailsFragmentViewModel =
            ViewModelProvider(this, factory).get(InvitationDetailsFragmentViewModel::class.java)

        binding.invitationViewModel = invitationDetailsFragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.invitationDetailsCourseItem.invitationViewModel = invitationDetailsFragmentViewModel
        binding.invitationDetailsCourseItem.lifecycleOwner = viewLifecycleOwner

        observeCourse()
    }

    private fun observeCourse() {
        invitationDetailsFragmentViewModel.course.observe(viewLifecycleOwner) { course ->
            if (course == null) {
                binding.invitationDetailsCourseHeader.visibility = View.GONE
                binding.invitationDetailsCourseItem.root.visibility = View.GONE
                binding.invitationDetailsCourseItem.invitationCourseItemTypeSpinner.setSelection(0)
                binding.invitationDetailsCourseItem.invitationCourseItemSubjects.text = null
                binding.invitationDetailsCourseItem.invitationCourseItemChipGroup.removeAllViews()

                binding.invitationDetailsAddCourseButton.visibility = View.VISIBLE
            } else {
                binding.invitationDetailsCourseItem.root.visibility = View.VISIBLE

                binding.invitationDetailsAddCourseButton.visibility = View.GONE
            }
        }
    }

    private fun initTypeRadio() {

        when (invitationDetailsFragmentViewModel.friendInvitation.value!!.invitationType) {
            FirestoreFriendInvitationContract.TYPE_STUDENT -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_student
            )

            FirestoreFriendInvitationContract.TYPE_TUTOR -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_tutor
            )
        }
    }

    private fun onSendClicked() {
        if (invitationDetailsFragmentViewModel.course.value != null
            && (invitationDetailsFragmentViewModel.course.value!!.subject == null || invitationDetailsFragmentViewModel.course.value!!.meetingDates == null)
        ) {
            Toast.makeText(activity, "Uzupełnij szczegóły bądź usuń zajęcia", Toast.LENGTH_SHORT)
                .show()
            Log.d(
                TAG,
                "onSendClicked: ${invitationDetailsFragmentViewModel.course.value!!.subject}"
            )
            if (invitationDetailsFragmentViewModel.course.value!!.meetingDates == null) Log.d(
                TAG,
                "onSendClicked: no meeting time selected"
            )
        } else {
            invitationDetailsFragmentViewModel.sendInvitation()
            this.findNavController().navigate(R.id.action_invitationDetails_to_home)
        }
    }

    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        if (adapterView != null) {
            //TODO fix the way the course type is assigned
            invitationDetailsFragmentViewModel.setCourseType(
                adapterView.getItemAtPosition(position).toString()
            )
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun onAddMeetingButtonClicked() {
        val dialog = MeetingPickerDialogFragment { meetingDate: WeekDate ->
            saveMeetingTime(meetingDate)
        }
        dialog.show(childFragmentManager, "meetingPicker")
    }

    private fun onRemoveCourseClicked() {
        invitationDetailsFragmentViewModel.removeCourse()
    }

    private fun saveMeetingTime(meetingDate: WeekDate) {
        val chip = Chip(activity)
        chip.text = meetingDate.toString()
        binding.invitationDetailsCourseItem.invitationCourseItemChipGroup.addView(chip)

        invitationDetailsFragmentViewModel.addMeetingDate(meetingDate)
    }
}