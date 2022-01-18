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
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentInvitationDetailsBinding
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.util.Util
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.MeetingPickerDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationDetailsFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.InvitationDetailsFragmentViewModelFactory

class InvitationDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val TAG = "InvitationDetailsFragme"

    private lateinit var binding: FragmentInvitationDetailsBinding

    private lateinit var mInvitationDetailsFragmentViewModel: InvitationDetailsFragmentViewModel

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
            mInvitationDetailsFragmentViewModel.addCourse(type)
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

        mInvitationDetailsFragmentViewModel =
            ViewModelProvider(this, factory).get(InvitationDetailsFragmentViewModel::class.java)

        binding.invitationViewModel = mInvitationDetailsFragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.invitationDetailsCourseItem.invitationViewModel = mInvitationDetailsFragmentViewModel
        binding.invitationDetailsCourseItem.lifecycleOwner = viewLifecycleOwner

        observeCourse()
    }

    private fun observeCourse() {
        mInvitationDetailsFragmentViewModel.course.observe(viewLifecycleOwner, { course ->
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
        })
    }

    private fun initTypeRadio() {

        when (mInvitationDetailsFragmentViewModel.friendInvitation.value!!.invitationType) {
            FirestoreFriendInvitationContract.TYPE_STUDENT -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_student
            )

            FirestoreFriendInvitationContract.TYPE_TUTOR -> binding.invitationDetailsTypeRadio.check(
                R.id.invitation_details_type_tutor
            )
        }
    }

    private fun onSendClicked() {
        if (mInvitationDetailsFragmentViewModel.course.value != null
            && (mInvitationDetailsFragmentViewModel.course.value!!.subject == null || mInvitationDetailsFragmentViewModel.course.value!!.meetingsDates == null)
        ) {
            Toast.makeText(activity, "Uzupełnij szczegóły bądź usuń zajęcia", Toast.LENGTH_SHORT)
                .show()
            Log.d(TAG, "onSendClicked: ${mInvitationDetailsFragmentViewModel.course.value!!.subject}")
            if (mInvitationDetailsFragmentViewModel.course.value!!.meetingsDates == null) Log.d(
                TAG,
                "onSendClicked: no meeting time selected"
            )
        } else {
            mInvitationDetailsFragmentViewModel.sendInvitation()
            findNavController(this).navigate(R.id.action_invitationDetails_to_home)
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
            mInvitationDetailsFragmentViewModel.setCourseType(
                adapterView.getItemAtPosition(position).toString()
            )
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
        mInvitationDetailsFragmentViewModel.removeCourse()
    }

    private fun saveMeetingTime(weekDay: String, hour: Int, minute: Int) {
        val chip = Chip(activity)
        val meetingDate = Util.formatMeetingTime(weekDay, hour, minute)
        chip.text = meetingDate
        binding.invitationDetailsCourseItem.invitationCourseItemChipGroup.addView(chip)

        mInvitationDetailsFragmentViewModel.addMeetingDate(meetingDate!!)
    }
}