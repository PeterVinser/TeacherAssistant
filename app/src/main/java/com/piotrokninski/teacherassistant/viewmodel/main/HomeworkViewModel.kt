package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.HomeworkAdapter
import kotlinx.coroutines.launch

class HomeworkViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    var viewType: String? = null

    private val _homeworkItems = MutableLiveData<List<HomeworkAdapter.Item>>()
    val homeworkItems: LiveData<List<HomeworkAdapter.Item>> = _homeworkItems

    init {
        viewModelScope.launch {
            viewType = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)
            getHomework()
        }
    }

    private suspend fun getHomework() {

        val homework = when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT ->
                FirestoreHomeworkRepository.getHomework(currentUserId, Homework.Contract.STUDENT_ID)

            AppConstants.VIEW_TYPE_TUTOR ->
                FirestoreHomeworkRepository.getHomework(currentUserId, Homework.Contract.TUTOR_ID)

            else -> throw IllegalArgumentException("Unknown viewType")
        }

        val assignedHomework = homework?.filter { it.status == Homework.Contract.STATUS_ASSIGNED }

        val undeliveredHomework = homework?.filter { it.status == Homework.Contract.STATUS_UNDELIVERED }

        val completedHomework = homework?.filter { it.status == Homework.Contract.STATUS_COMPLETED }

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

    class Factory(private val dataStoreRepository: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeworkViewModel::class.java)) {
                return HomeworkViewModel(dataStoreRepository) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}