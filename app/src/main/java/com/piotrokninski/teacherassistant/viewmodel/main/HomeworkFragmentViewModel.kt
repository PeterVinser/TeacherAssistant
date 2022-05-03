package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.HomeworkAdapterItem
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HomeworkFragmentViewModel : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    var viewType: String = MainPreferences.getViewType()
        ?: throw IllegalStateException("The view type has not been initialized")

    private val _homeworkItems = MutableLiveData<List<HomeworkAdapterItem>>()
    val homeworkItems: LiveData<List<HomeworkAdapterItem>> = _homeworkItems

    init {
        viewModelScope.launch {
            getHomework()
        }
    }

    private suspend fun getHomework() {

        val assignedHomework: ArrayList<Homework>? = FirestoreHomeworkRepository.getHomework(
            currentUserId,
            viewType,
            Homework.STATUS_ASSIGNED
        )
        val undeliveredHomework: ArrayList<Homework>? = FirestoreHomeworkRepository.getHomework(
            currentUserId,
            viewType,
            Homework.STATUS_UNDELIVERED
        )
        val completedHomework: ArrayList<Homework>? = FirestoreHomeworkRepository.getHomework(
            currentUserId,
            viewType,
            Homework.STATUS_COMPLETED
        )

        val homeworkItems = ArrayList<HomeworkAdapterItem>()

        if (!assignedHomework.isNullOrEmpty()) {
            val titleId = when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> R.string.homework_assigned_student_title

                AppConstants.VIEW_TYPE_TUTOR -> R.string.homework_assigned_tutor_title

                else -> throw IllegalArgumentException("Unknown viewType")
            }

            homeworkItems.add(HomeworkAdapterItem.HeaderItem(titleId))

            assignedHomework.forEach {
                homeworkItems.add(HomeworkAdapterItem.HomeworkItem(it))
            }
        }

        if (!undeliveredHomework.isNullOrEmpty()) {
            homeworkItems.add(HomeworkAdapterItem.HeaderItem(R.string.homework_undelivered_title))

            undeliveredHomework.forEach {
                homeworkItems.add(HomeworkAdapterItem.HomeworkItem(it))
            }
        }

        if (!completedHomework.isNullOrEmpty()) {
            homeworkItems.add(HomeworkAdapterItem.HeaderItem(R.string.homework_completed_title))

            completedHomework.forEach {
                homeworkItems.add(HomeworkAdapterItem.HomeworkItem(it))
            }
        }

        _homeworkItems.value = homeworkItems
    }
}