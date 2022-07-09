package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentUserProfileBinding
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.view.main.MainActivity
import com.piotrokninski.teacherassistant.view.main.dialog.InvitationDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.main.UserProfileFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.factory.UserProfileFragmentViewModelFactory

class UserProfileFragment : Fragment() {
    private val TAG = "UserProfileFragment"

    private lateinit var binding: FragmentUserProfileBinding

    private lateinit var userProfileViewModel: UserProfileFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).isBottomNavVisible(false)

        arguments?.let {
            val safeArgs = UserProfileFragmentArgs.fromBundle(it)
            setupViewModel(safeArgs.userId)
        }

        binding.userProfileInviteButton.setOnClickListener { onInviteClicked() }
        binding.userProfileRejectButton.setOnClickListener { onRejectClicked() }
    }

    private fun onInviteClicked() {
        if (userProfileViewModel.friendStatus.value == Friend.STATUS_BLANK) {
            val dialog = InvitationDialogFragment (
                { invitedAs: String, invitationMessage: String? -> sendInvitation(invitedAs, invitationMessage) },
                userProfileViewModel.currentUser,
                userProfileViewModel.user.value!!
            )
            dialog.show(childFragmentManager, "invitationDialog")
        } else {
            userProfileViewModel.onInviteButtonClicked()
        }
    }

    private fun onRejectClicked() {
        userProfileViewModel.rejectInvitation()
    }

    private fun sendInvitation(invitationType: String, invitationMessage: String?) {
        if (invitationType == Invitation.Contract.STUDENT) {

            val action = UserProfileFragmentDirections.actionUserProfileToInvitation(
                Invitation.Contract.TYPE_FRIENDSHIP,
                userProfileViewModel.prepareInvitation(invitationType, invitationMessage)
            )
            this.findNavController().navigate(action)
        } else {
            userProfileViewModel.sendInvitation(invitationType, invitationMessage)
        }
    }

    private fun setupViewModel(userId: String) {
        val factory = UserProfileFragmentViewModelFactory(userId)

        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileFragmentViewModel::class.java)

        binding.userProfileViewModel = userProfileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeFriendStatus()

        observeSearchedUser()

        observeFriendInvitation()
    }

    private fun observeFriendStatus() {
        userProfileViewModel.friendStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Friend.STATUS_BLANK ->
                    binding.userProfileInviteButton.text =
                        getString(R.string.user_profile_invite_button_text)

                Friend.STATUS_INVITED ->
                    binding.userProfileInviteButton.text =
                        getString(R.string.user_profile_cancel_button_text)

                Friend.STATUS_INVITING ->
                    binding.userProfileInviteButton.text =
                        getString(R.string.user_profile_approve_button_text)

                Friend.STATUS_APPROVED ->
                    binding.userProfileInviteButton.text =
                        getString(R.string.user_profile_delete_button_text)

                Friend.STATUS_BLOCKED ->
                    binding.userProfileInviteButton.visibility = View.GONE
            }

            if (status == Friend.STATUS_INVITING) {
                binding.userProfileRejectButton.visibility = View.VISIBLE
            } else {
                binding.userProfileRejectButton.visibility = View.GONE
            }
        }
    }

    private fun observeSearchedUser() {
        userProfileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user.student && user.tutor) {
                binding.userProfileProfession.text = getString(R.string.student_and_tutor_title_text)
            } else if (user.student) {
                binding.userProfileProfession.text = getString(R.string.student_title_text)
            } else if (user.tutor) {
                binding.userProfileProfession.text = getString(R.string.tutor_title_text)
            }
        }
    }

    private fun observeFriendInvitation() {
        userProfileViewModel.invitation.observe(viewLifecycleOwner) { invitation ->
            if (invitation != null) {
                binding.userProfileInvitation.visibility = View.VISIBLE

                binding.userProfileInvitationDescription.text = when (invitation.invitedAs) {
                    Invitation.Contract.STUDENT -> getString(R.string.invitation_student_type)

                    Invitation.Contract.TUTOR -> getString(R.string.invitation_tutor_type)

                    Invitation.Contract.FRIEND -> getString(R.string.invitation_friend_type)

                    else -> throw IllegalArgumentException("Unknown invitation type")
                }

                if (invitation.course != null) {
                    binding.userProfileInvitationCourse.root.visibility = View.VISIBLE

                    binding.userProfileInvitationCourse.course = invitation.course

                    invitation.course!!.weekDates?.forEach { date ->
                        val chip = Chip(context)
                        chip.text = date.toString()

                        binding.userProfileInvitationCourse.invitationItemCourseDates.addView(chip)
                    }
                }
            } else {
                binding.userProfileInvitation.visibility = View.GONE
            }
        }
    }
}