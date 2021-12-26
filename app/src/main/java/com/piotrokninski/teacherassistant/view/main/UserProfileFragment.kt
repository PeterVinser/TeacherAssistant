package com.piotrokninski.teacherassistant.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.FragmentUserProfileBinding
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.view.main.dialog.InvitationDialogFragment
import com.piotrokninski.teacherassistant.viewmodel.UserProfileFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.factory.UserProfileFragmentViewModelFactory

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
        if (userProfileViewModel.friendStatus.value == FirestoreFriendContract.STATUS_BLANK) {
            val dialog = InvitationDialogFragment {
                    invitationType: String, invitationMessage: String? -> sendInvitation(invitationType, invitationMessage)
            }
            dialog.show(childFragmentManager, "invitationDialog")
        } else {
            userProfileViewModel.onInviteButtonClicked()
        }
    }

    private fun onRejectClicked() {
        userProfileViewModel.rejectInvitation()
    }

    private fun sendInvitation(invitationType: String, invitationMessage: String?) {
        if (invitationType == FirestoreFriendInvitationContract.TYPE_FRIEND) {
            userProfileViewModel.sendInvitation(invitationType, invitationMessage)
        } else {

            val action = UserProfileFragmentDirections.actionUserProfileToInvitationDetails(userProfileViewModel.prepareInvitation(invitationType, invitationMessage))
            findNavController(this).navigate(action)
        }
    }

    private fun setupViewModel(userId: String) {
        val factory = UserProfileFragmentViewModelFactory(userId)

        userProfileViewModel = ViewModelProvider(this, factory).get(UserProfileFragmentViewModel::class.java)

        binding.userProfileViewModel = userProfileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeFriendStatus()
    }

    private fun observeFriendStatus() {
        userProfileViewModel.friendStatus.observe(viewLifecycleOwner, { status ->
            when (status) {
                FirestoreFriendContract.STATUS_BLANK ->
                    binding.userProfileInviteButton.text = getString(R.string.user_profile_invite_button_text)

                FirestoreFriendContract.STATUS_INVITED ->
                    binding.userProfileInviteButton.text = getString(R.string.user_profile_cancel_button_text)

                FirestoreFriendContract.STATUS_INVITING ->
                    binding.userProfileInviteButton.text = getString(R.string.user_profile_approve_button_text)

                FirestoreFriendContract.STATUS_APPROVED ->
                    binding.userProfileInviteButton.text = getString(R.string.user_profile_delete_button_text)

                FirestoreFriendContract.STATUS_BLOCKED ->
                    binding.userProfileInviteButton.visibility = View.GONE
            }

            if (status == FirestoreFriendContract.STATUS_INVITING) {
                binding.userProfileRejectButton.visibility = View.VISIBLE
            } else {
                binding.userProfileRejectButton.visibility = View.GONE
            }
            Log.d(TAG, "observeFriendStatus: $status")
        })
    }
}