package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.HomeworkAdapter
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HomeworkViewModel : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    var viewType: String = MainPreferences.getViewType()
        ?: throw IllegalStateException("The view type has not been initialized")

    private val _homeworkItems = MutableLiveData<List<HomeworkAdapter.Item>>()
    val homeworkItems: LiveData<List<HomeworkAdapter.Item>> = _homeworkItems

    init {
        viewModelScope.launch {
            getHomework()
        }
    }

    private suspend fun getHomework() {

        val homework = FirestoreHomeworkRepository.getHomework(currentUserId, viewType)

        val assignedHomework = homework?.filter { it.status == Homework.STATUS_ASSIGNED }

        val undeliveredHomework = homework?.filter { it.status == Homework.STATUS_UNDELIVERED }

        val completedHomework = homework?.filter { it.status == Homework.STATUS_COMPLETED }

        val homeworkItems = ArrayList<HomeworkAdapter.Item>()

        if (!assignedHomework.isNullOrEmpty()) {
            val titleId = when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> R.string.homework_assigned_student_title

                AppConstants.VIEW_TYPE_TUTOR -> R.string.homework_assigned_tutor_title

                else -> throw IllegalArgumentException("Unknown viewType")
            }

            homeworkItems.add(HomeworkAdapter.Item.Header(titleId))

            assignedHomework.forEach {
                homeworkItems.add(HomeworkAdapter.Item.Homework(it))
            }
        }

        if (!undeliveredHomework.isNullOrEmpty()) {
            homeworkItems.add(HomeworkAdapter.Item.Header(R.string.homework_undelivered_title))

            undeliveredHomework.forEach {
                homeworkItems.add(HomeworkAdapter.Item.Homework(it))
            }
        }

        if (!completedHomework.isNullOrEmpty()) {
            homeworkItems.add(HomeworkAdapter.Item.Header(R.string.homework_completed_title))

            completedHomework.forEach {
                homeworkItems.add(HomeworkAdapter.Item.Homework(it))
            }
        }

        _homeworkItems.value = homeworkItems
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeworkViewModel::class.java)) {
                return HomeworkViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}