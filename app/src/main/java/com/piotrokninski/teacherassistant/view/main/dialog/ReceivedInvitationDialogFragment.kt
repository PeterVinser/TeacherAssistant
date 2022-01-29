package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.cloudfunctions.InvitationCloudFunctions
import com.piotrokninski.teacherassistant.databinding.ReceivedInvitationDialogBinding
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem

class ReceivedInvitationDialogFragment(private val invitation: HomeAdapterItem.Invitation,
                                       private val profileCallback: (invitation: HomeAdapterItem.Invitation) -> Unit,
                                       private val detailsCallback: (invitation: HomeAdapterItem.Invitation) -> Unit,
                                       private val refreshCallback: () -> Unit): DialogFragment() {

    private lateinit var binding: ReceivedInvitationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ReceivedInvitationDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.friendInvitation = invitation.friendInvitation
        binding.receivedInvitationDialogType.text = invitation.getInvitationType()

        if (invitation.friendInvitation.invitationMessage.isNullOrEmpty()) {
            binding.receivedInvitationDialogMessage.visibility = View.GONE
        }

        if (invitation.friendInvitation.courseIds == null) {
            binding.receivedInvitationDialogDetailsButton.visibility = View.GONE
        }

        binding.receivedInvitationDialogRejectButton.setOnClickListener {
            InvitationCloudFunctions.rejectFriendInvitation(invitation.friendInvitation)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogConfirmButton.setOnClickListener {
            InvitationCloudFunctions.approveFriendInvitation(invitation.friendInvitation)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogProfileButton.setOnClickListener {
            profileCallback(invitation)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogDetailsButton.setOnClickListener {
            detailsCallback(invitation)
            refreshCallback
            dismiss()
        }
    }
}