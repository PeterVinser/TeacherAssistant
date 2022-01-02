package com.piotrokninski.teacherassistant.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.model.Course
import com.piotrokninski.teacherassistant.model.Note
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreNoteRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class CourseDetailsFragmentViewModel(course: Course): ViewModel() {
    private val TAG = "CourseDetailsFragmentVi"

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    lateinit var viewType: String

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> = _course

    init {
        viewType = MainPreferences.getViewType() ?: throw IllegalStateException("The view type has not been initialized")

        viewModelScope.launch {

            _notes.value = FirestoreNoteRepository.getCourseNotes(course.courseId)
        }

        _course.value = course
    }

    fun addNote(note: Note) {
        FirestoreNoteRepository.setNote(note)

        val auxList = ArrayList<Note>()

        if (notes.value != null) {
            auxList.addAll(notes.value!!)
        }

        auxList.add(note)
        _notes.value = auxList
    }
}