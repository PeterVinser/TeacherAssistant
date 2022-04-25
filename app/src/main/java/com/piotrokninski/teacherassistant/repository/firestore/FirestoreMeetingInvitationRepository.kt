package com.piotrokninski.teacherassistant.repository.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreMeetingInvitationContract
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation

object FirestoreMeetingInvitationRepository {

    fun addMeetingInvitation(meetingInvitation: MeetingInvitation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreMeetingInvitationContract.COLLECTION_NAME).add(meetingInvitation)
    }
}