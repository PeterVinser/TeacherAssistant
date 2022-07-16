package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.databinding.ReceivedInvitationDialogBinding
import com.piotrokninski.teacherassistant.view.main.adapter.InvitationsAdapter

class ReceivedInvitationDialogFragment(
    private val invitationItem: InvitationsAdapter.Item.Invitation,
    private val profileCallback: (invitationItem: InvitationsAdapter.Item.Invitation) -> Unit,
    private val detailsCallback: (invitationItem: InvitationsAdapter.Item.Invitation) -> Unit,
    private val refreshCallback: () -> Unit): DialogFragment()
{

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
//        binding.friendInvitation = friendInvitationItem.friendInvitation
//        binding.receivedInvitationDialogType.text = friendInvitationItem.getInvitationType()
//
//        if (friendInvitationItem.friendInvitation.invitationMessage.isNullOrEmpty()) {
//            binding.receivedInvitationDialogMessage.visibility = View.GONE
//        }
//
//        if (friendInvitationItem.friendInvitation.course == null) {
//            binding.receivedInvitationDialogDetailsButton.visibility = View.GONE
//        }
//
//        binding.receivedInvitationDialogRejectButton.setOnClickListener {
////            InvitationCloudFunctions.rejectFriendInvitation(invitationItem.friendInvitation)
//            refreshCallback
//            dismiss()
//        }
//
//        binding.receivedInvitationDialogConfirmButton.setOnClickListener {
////            InvitationCloudFunctions.approveFriendInvitation(invitationItem.friendInvitation)
//            refreshCallback
//            dismiss()
//        }

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