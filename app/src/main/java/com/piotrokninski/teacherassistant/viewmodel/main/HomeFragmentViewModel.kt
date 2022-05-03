package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingInvitationRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.fragment.HomeFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeFragmentViewModel : ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    val viewType = MainPreferences.getViewType()
        ?: throw IllegalStateException("No specified viewType")

    private val _homeFeedItems = MutableLiveData<List<HomeAdapterItem>>()
    val homeFeedItems: LiveData<List<HomeAdapterItem>> = _homeFeedItems

    private val eventChannel = Channel<HomeEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            val invitations =
                FirestoreFriendInvitationRepository.getReceivedFriendsInvitations(
                    currentUserId,
                    FriendInvitation.STATUS_PENDING
                )

            val pendingCourses = when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> FirestoreCourseRepository.getStudiedCourses(
                    currentUserId,
                    Course.STATUS_PENDING
                )

                AppConstants.VIEW_TYPE_TUTOR -> FirestoreCourseRepository.getTaughtCourses(
                    currentUserId,
                    Course.STATUS_PENDING
                )

                else -> throw IllegalArgumentException("Unknown viewType")
            }

            Log.d(TAG, "getItems: $viewType")

            val assignedHomework = FirestoreHomeworkRepository.getHomework(
                currentUserId,
                viewType,
                Homework.STATUS_ASSIGNED
            )

            val receivedMeetingInvitations =
                FirestoreMeetingInvitationRepository.getReceivedMeetingInvitations(
                    currentUserId,
                    MeetingInvitation.STATUS_PENDING
                )
            val sentMeetingInvitation =
                FirestoreMeetingInvitationRepository.getSentMeetingInvitations(
                    currentUserId,
                    MeetingInvitation.STATUS_PENDING
                )
            val auxList = ArrayList<HomeAdapterItem>()

            if (!invitations.isNullOrEmpty()) {
                val invitationHeader = HomeAdapterItem.HeaderItem(R.string.invitation_header_text)

                auxList.add(invitationHeader)

                invitations.forEach { friendInvitation ->
                    auxList.add(HomeAdapterItem.FriendInvitationItem(friendInvitation))
                }
            }

            pendingCourses?.let {
                auxList.add(HomeAdapterItem.HeaderItem(R.string.pending_course_header_text))

                it.forEach { course ->
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

            if (receivedMeetingInvitations != null || sentMeetingInvitation != null) {
                val titleId = R.string.home_meeting_invitation_header_text

                auxList.add(HomeAdapterItem.HeaderItem(titleId))

                receivedMeetingInvitations?.let {
                    auxList.add(HomeAdapterItem.HeaderItem(R.string.home_meeting_received_invitation_header_text))
                    it.forEach { invitation ->
                        auxList.add(
                            HomeAdapterItem.MeetingInvitationItem(
                                invitation,
                                currentUserId == invitation.invitedUserId
                            )
                        )
                    }
                }

                sentMeetingInvitation?.let {
                    auxList.add(HomeAdapterItem.HeaderItem(R.string.home_meeting_sent_invitation_header_text))
                    it.forEach { invitation ->
                        auxList.add(
                            HomeAdapterItem.MeetingInvitationItem(
                                invitation,
                                currentUserId == invitation.invitedUserId
                            )
                        )
                    }
                }
            }

            _homeFeedItems.value = auxList
        }
    }

    fun itemPositiveAction(homeAdapterItem: HomeAdapterItem) {
        when (homeAdapterItem) {
            is HomeAdapterItem.FriendInvitationItem -> homeAdapterItem.friendInvitation.invitationId?.let { id ->
                FirestoreFriendInvitationRepository.updateFriendInvitation(
                    id,
                    FriendInvitation.STATUS,
                    FriendInvitation.STATUS_APPROVED
                )
            }

            is HomeAdapterItem.CourseItem -> {
                when (viewType) {
                    AppConstants.VIEW_TYPE_STUDENT -> homeAdapterItem.course.courseId?.let { id ->
                        FirestoreCourseRepository.updateCourse(
                            id,
                            Course.STATUS,
                            Course.STATUS_APPROVED
                        )
                    }

                    AppConstants.VIEW_TYPE_TUTOR -> viewModelScope.launch {
                        eventChannel.send(
                            HomeEvent.EditItemEvent(
                                homeAdapterItem.id,
                                homeAdapterItem
                            )
                        )
                    }
                }
            }

            is HomeAdapterItem.MeetingInvitationItem -> {
                if (homeAdapterItem.invited) {
                    homeAdapterItem.meetingInvitation.id?.let { id ->
                        FirestoreMeetingInvitationRepository.updateMeetingInvitation(
                            id,
                            MeetingInvitation.STATUS,
                            MeetingInvitation.STATUS_APPROVED
                        )
                    }
                } else {
                    viewModelScope.launch {
                        homeAdapterItem.meetingInvitation.id?.let {
                            Log.d(TAG, "itemPositiveAction: $it")
                            HomeEvent.EditItemEvent(it, homeAdapterItem)
                        }?.let { eventChannel.send(it) }
                    }
                }
            }

            is HomeAdapterItem.HomeworkItem -> {}

            else -> {}
        }
    }

    fun itemNegativeAction(homeAdapterItem: HomeAdapterItem) {
        when (homeAdapterItem) {
            is HomeAdapterItem.FriendInvitationItem -> homeAdapterItem.friendInvitation.invitationId?.let { id ->
                FirestoreFriendInvitationRepository.updateFriendInvitation(
                    id,
                    FriendInvitation.STATUS,
                    FriendInvitation.STATUS_REJECTED
                )
            }

            is HomeAdapterItem.CourseItem -> FirestoreCourseRepository.deleteCourse(
                homeAdapterItem.course.courseId!!
            )

            is HomeAdapterItem.HomeworkItem -> {}

            is HomeAdapterItem.MeetingInvitationItem -> {
                if (homeAdapterItem.invited) {
                    homeAdapterItem.meetingInvitation.id?.let { id ->
                        FirestoreMeetingInvitationRepository.updateMeetingInvitation(
                            id,
                            MeetingInvitation.STATUS,
                            MeetingInvitation.STATUS_REJECTED
                        )
                    }
                } else {
                    homeAdapterItem.meetingInvitation.id?.let { id ->
                        FirestoreMeetingInvitationRepository.deleteMeetingInvitation(id)
                    }
                }
            }

            else -> {}
        }
    }

    sealed class HomeEvent() {
        data class EditItemEvent(val itemId: String, val homeAdapterItem: HomeAdapterItem) :
            HomeEvent()
    }
}