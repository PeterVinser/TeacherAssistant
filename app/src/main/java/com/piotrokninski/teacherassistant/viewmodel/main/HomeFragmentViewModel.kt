package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreHomeworkContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class HomeFragmentViewModel : ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    val viewType = MainPreferences.getViewType()
        ?: throw IllegalStateException("No specified viewType")

    private val _homeFeedItems = MutableLiveData<List<HomeAdapterItem>>()
    val homeFeedItems: LiveData<List<HomeAdapterItem>> = _homeFeedItems

    init {

        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            val invitations =
                FirestoreFriendInvitationRepository.getReceivedFriendsInvitations(currentUserId)

            val pendingCourses = when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> FirestoreCourseRepository.getStudiedCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_PENDING
                )

                AppConstants.VIEW_TYPE_TUTOR -> FirestoreCourseRepository.getTaughtCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_PENDING
                )

                else -> throw IllegalArgumentException("Unknown viewType")
            }

            val assignedHomework = FirestoreHomeworkRepository.getHomework(
                currentUserId,
                viewType,
                FirestoreHomeworkContract.STATUS_ASSIGNED
            )

            val auxList = ArrayList<HomeAdapterItem>()

            if (!invitations.isNullOrEmpty()) {
                val invitationHeader = HomeAdapterItem.HeaderItem(R.string.invitation_header_text)

                auxList.add(invitationHeader)

                invitations.forEach { friendInvitation ->
                    val attachedCourse = pendingCourses?.filter { it.courseId == friendInvitation.courseId }
                    if (attachedCourse != null) {
                        pendingCourses.removeAll(attachedCourse)
                    }

                    auxList.add(HomeAdapterItem.InvitationItem(friendInvitation, attachedCourse?.get(0)))
                }
            }

            if (!pendingCourses.isNullOrEmpty()) {
                val courseHeader = HomeAdapterItem.HeaderItem(R.string.pending_course_header_text)

                auxList.add(courseHeader)

                pendingCourses.forEach { course ->
                    auxList.add(HomeAdapterItem.CourseItem(course))
                }
            }

            if (!assignedHomework.isNullOrEmpty()) {
                val titleId = when (viewType) {
                    AppConstants.VIEW_TYPE_STUDENT -> R.string.homework_student_header_text

                    AppConstants.VIEW_TYPE_TUTOR -> R.string.homework_tutor_header_text

                    else -> throw IllegalArgumentException("Unknown viewType")
                }

                auxList.add(HomeAdapterItem.HeaderItem(titleId))

                assignedHomework.forEach { homework ->
                    auxList.add(HomeAdapterItem.HomeworkItem(homework))
                }
            }

            _homeFeedItems.value = auxList
        }
    }

    fun deleteCourse(courseId: String) {
        FirestoreCourseRepository.deleteCourse(courseId)
    }
}