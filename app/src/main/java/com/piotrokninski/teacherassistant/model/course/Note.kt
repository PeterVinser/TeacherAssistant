package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreNoteContract
import java.lang.Exception

data class Note(
    val courseId: String,
    val studentId: String,
    val tutorId: String,
    val studentFullName: String,
    val tutorFullName: String,
    val topic: String,
    val subject: String,
    val date: String,
    val note: String
) {

    companion object {
        fun DocumentSnapshot.toNote(): Note? {
            return try {

                val courseId = getString(FirestoreNoteContract.COURSE_ID)!!
                val studentId = getString(FirestoreNoteContract.STUDENT_ID)!!
                val tutorId = getString(FirestoreNoteContract.TUTOR_ID)!!
                val studentFullName = getString(FirestoreNoteContract.STUDENT_FULL_NAME)!!
                val tutorFullName = getString(FirestoreNoteContract.TUTOR_FULL_NAME)!!
                val topic = getString(FirestoreNoteContract.TOPIC)!!
                val subject = getString(FirestoreNoteContract.SUBJECT)!!
                val date = getString(FirestoreNoteContract.DATE)!!
                val note = getString(FirestoreNoteContract.NOTE)!!

                Note(courseId, studentId, tutorId, studentFullName, tutorFullName, topic, subject, date, note)

            } catch (e: Exception) {
                Log.e(TAG, "toNote: ", e)
                null
            }
        }
        private const val TAG = "Note"
    }
}
