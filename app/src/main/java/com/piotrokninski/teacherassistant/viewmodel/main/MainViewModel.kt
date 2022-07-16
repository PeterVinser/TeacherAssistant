package com.piotrokninski.teacherassistant.viewmodel.main

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.UserHint
import com.piotrokninski.teacherassistant.repository.calendar.CalendarProvider
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomMeetingRepository
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainActivityViewModel"

    private val userRepository: RoomUserRepository
    private val meetingRepository: RoomMeetingRepository

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val eventChannel = Channel<MainEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {

        val roomInstance = AppDatabase.getInstance()

        userRepository = RoomUserRepository(roomInstance.userDao)

        meetingRepository = RoomMeetingRepository(roomInstance.meetingDAO)

        viewModelScope.launch {
            _viewType.value = MainPreferences.getViewType()

            //TODO: delete unnecessary user Room instance
            saveCurrentUserData()

            eventChannel.send(MainEvent.CalendarPermissionEvent)
        }
    }

    fun initUser(user: User) {
        viewModelScope.launch {

            val userHint = UserHint.createHint(user.userId, user.fullName)

            FirestoreUserRepository.setUserData(user)
            FirestoreUserHintRepository.setUserHintData(userHint)

            userRepository.insertUser(user)
        }
    }

    private suspend fun saveCurrentUserData() {
        val currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId)

        if (currentUser != null) {
            userRepository.insertUser(currentUser)

            if (!(currentUser.student && currentUser.tutor)) {
                if (currentUser.student) {
                    updateViewType(AppConstants.VIEW_TYPE_STUDENT)
                } else if (currentUser.tutor) {
                    updateViewType(AppConstants.VIEW_TYPE_TUTOR)
                }
            } else {
                updateViewType(AppConstants.VIEW_TYPE_TUTOR)
            }
        }
    }

    private fun updateViewType(viewType: String) {
        MainPreferences.updateViewType(viewType)

        _viewType.value = viewType
    }

    fun updateCalendar() {
        viewModelScope.launch {
            val now = Calendar.getInstance().timeInMillis
            meetingRepository.deleteMeetingsBefore(now)

            FirestoreMeetingRepository.getUpcomingMeetings(currentUserId)?.let { meetings ->
                syncMeetings(meetings)
            }
        }
    }

    private suspend fun syncMeetings(meetings: ArrayList<Meeting>) {
        val savedMeetings = ArrayList(meetingRepository.getAllMeetings())

        meetings.forEach { meeting ->
            savedMeetings.filter { it.id == meeting.id }.ifEmpty {
                if (meeting.singular) {
                    meeting.calendarId = CalendarProvider.insertMeeting(getApplication(), meeting)
                    meetingRepository.insertMeeting(meeting)
                } else {
                    meeting.weekDates?.forEach { weekDate ->
                        weekDate.calendarId = CalendarProvider.insertWeekDate(
                            getApplication(),
                            meeting.title!!,
                            meeting.description,
                            weekDate
                        )
                    }
                }
                null
            }?.forEach { savedMeeting ->
                if (meeting.singular) {
                    if (!equals(meeting, savedMeeting)) {
                        meeting.roomId = savedMeeting.roomId
                        meeting.calendarId = savedMeeting.calendarId
                        CalendarProvider.updateMeeting(getApplication(), meeting)
                        meetingRepository.updateMeeting(meeting)
                    }
                } else {
                    // TODO: fix the weekDates and complete sync implementation
                    if (!equals(meeting, savedMeeting)) {
                        savedMeeting.weekDates?.forEach { weekDate ->
                            CalendarProvider.deleteWeekDate(getApplication(), weekDate)
                        }
                        meeting.weekDates?.forEach { weekDate ->
                            weekDate.calendarId = CalendarProvider.insertWeekDate(
                                getApplication(),
                                meeting.title!!,
                                meeting.description,
                                weekDate
                            )
                        }
                    }
                }

                meetings.remove(meeting)
                savedMeetings.remove(savedMeeting)
            }
        }

        savedMeetings.forEach { meeting ->
            CalendarProvider.deleteMeeting(getApplication(), meeting)
        }

        meetingRepository.deleteMeetings(savedMeetings)
    }

    private fun equals(that: Meeting, other: Meeting): Boolean =
        that.title == other.title &&
        that.description == other.description &&
        that.date == other.date &&
        that.durationHours == other.durationHours &&
        that.durationMinutes == other.durationMinutes

    sealed class MainEvent {
        object CalendarPermissionEvent : MainEvent()
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}