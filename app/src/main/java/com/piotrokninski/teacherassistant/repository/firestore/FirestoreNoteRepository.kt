package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.course.Note
import com.piotrokninski.teacherassistant.model.course.Note.Companion.toNote
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreNoteContract
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreNoteRepository {
    private const val TAG = "FirestoreNoteRepository"

    fun setNote(note: Note) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreNoteContract.COLLECTION_NAME).document()
            .set(note)
    }

    suspend fun getCourseNotes(courseId: String): ArrayList<Note>? {
        val db = FirebaseFirestore.getInstance()

        val notesRef = db.collection(FirestoreNoteContract.COLLECTION_NAME)

        val query = notesRef.whereEqualTo(FirestoreNoteContract.COURSE_ID,courseId)

        return try {

            val notes = ArrayList<Note>()

            query.get().await().forEach { note ->
                note?.toNote()?.let { notes.add(it) }
            }

            if (notes.isEmpty()) {
                null
            } else {
                notes
            }

        } catch (e: Exception) {
            Log.e(TAG, "getCourseNotes: ", e)
            null
        }
    }
}