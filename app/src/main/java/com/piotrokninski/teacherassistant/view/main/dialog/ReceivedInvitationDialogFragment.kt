package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.cloudfunctions.InvitationCloudFunctions
import com.piotrokninski.teacherassistant.databinding.ReceivedInvitationDialogBinding
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem

class ReceivedInvitationDialogFragment(private val invitationItem: HomeAdapterItem.InvitationItem,
                                       private val profileCallback: (invitationItem: HomeAdapterItem.InvitationItem) -> Unit,
                                       private val detailsCallback: (invitationItem: HomeAdapterItem.InvitationItem) -> Unit,
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
        binding.friendInvitation = invitationItem.friendInvitation
        binding.receivedInvitationDialogType.text = invitationItem.getInvitationType()

        if (invitationItem.friendInvitation.invitationMessage.isNullOrEmpty()) {
            binding.receivedInvitationDialogMessage.visibility = View.GONE
        }

        if (invitationItem.friendInvitation.courseIds == null) {
            binding.receivedInvitationDialogDetailsButton.visibility = View.GONE
        }

        binding.receivedInvitationDialogRejectButton.setOnClickListener {
            InvitationCloudFunctions.rejectFriendInvitation(invitationItem.friendInvitation)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogConfirmButton.setOnClickListener {
            InvitationCloudFunctions.approveFriendInvitation(invitationItem.friendInvitation)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogProfileButton.setOnClickListener {
            profileCallback(invitationItem)
            refreshCallback
            dismiss()
        }

        binding.receivedInvitationDialogDetailsButton.setOnClickListener {
            detailsCallback(invitationItem)
            refreshCallback
            dismiss()
        }
    }
}