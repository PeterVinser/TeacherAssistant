package com.piotrokninski.teacherassistant.view.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.InvitationDialogBinding
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.user.User

class InvitationDialogFragment(
    private val callback: (invitationType: String, invitationMessage: String?) -> Unit,
    private val currentUser: User,
    private val searchedUser: User): DialogFragment() {

    private lateinit var binding: InvitationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = InvitationDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!searchedUser.student || !currentUser.tutor) {
            binding.invitationDialogTypeRadio.removeView(binding.invitationDialogRadioStudent)
        }

        if (!searchedUser.tutor || !currentUser.student) {
            binding.invitationDialogTypeRadio.removeView(binding.invitationDialogRadioTutor)
        }

        binding.invitationDialogNextButton.isEnabled = false

        binding.invitationDialogTypeRadio.setOnCheckedChangeListener { _, type ->
            if (type == R.id.invitation_dialog_radio_friend) {
                binding.invitationDialogNextButton.text = getString(R.string.invitation_send_text)
            } else {
                binding.invitationDialogNextButton.text = getString(R.string.invitation_dialog_details_text)
            }
            binding.invitationDialogNextButton.isEnabled = true
        }

        binding.invitationDialogCancelButton.setOnClickListener {
            dismiss()
        }

        binding.invitationDialogNextButton.setOnClickListener {
            val invitationMessage = binding.invitationDialogMessage.text.toString()

            when (binding.invitationDialogTypeRadio.checkedRadioButtonId) {
                R.id.invitation_dialog_radio_student -> callback(FriendInvitation.TYPE_STUDENT, invitationMessage)

                R.id.invitation_dialog_radio_tutor -> callback(FriendInvitation.TYPE_TUTOR, invitationMessage)

                R.id.invitation_dialog_radio_friend -> callback(FriendInvitation.TYPE_FRIEND, invitationMessage)
            }

            dismiss()
        }
    }
}