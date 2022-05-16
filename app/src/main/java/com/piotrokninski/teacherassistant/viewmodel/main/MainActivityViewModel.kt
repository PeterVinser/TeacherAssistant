package com.piotrokninski.teacherassistant.viewmodel.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.UserHint
import com.piotrokninski.teacherassistant.repository.calendar.CalendarProvider
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreRecurringMeetingsRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomMeetingRepository
import com.piotrokninski.teacherassistant.repository.room.repository.RoomRecurringMeetingRepository
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainActivityViewModel"

    private val userRepository: RoomUserRepository
    private val meetingRepository: RoomMeetingRepository
    private val recurringMeetingRepository: RoomRecurringMeetingRepository

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val eventChannel = Channel<MainEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {

        val roomInstance = AppDatabase.getInstance()

        userRepository = RoomUserRepository(roomInstance.userDao)

        meetingRepository = RoomMeetingRepository(roomInstance.meetingDAO)

        recurringMeetingRepository = RoomRecurringMeetingRepository(roomInstance.recurringMeetingDAO)

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
            meetingRepository.deleteCompletedMeetings(Calendar.getInstance().timeInMillis)

            FirestoreMeetingRepository.getUpcomingSingularMeetings(currentUserId)?.let { meetings ->
                syncMeetings(meetings)
            }

            FirestoreRecurringMeetingsRepository.getRecurringMeetings(currentUserId)?.let { recurringMeetings ->
                syncRecurringMeetings(recurringMeetings)
            }
        }
    }

    private suspend fun syncMeetings(meetings: ArrayList<Meeting>) {
        val savedMeetings = ArrayList(meetingRepository.getAllMeetings())
        savedMeetings.forEach {
            Log.d(TAG, "syncMeetings: saved $it")
        }
        val checkedMeetings = ArrayList<Meeting>()
        meetings.forEach { meeting ->
            val matchingMeetings = savedMeetings.filter { it.id == meeting.id }
            matchingMeetings.forEach {
                if (meeting != it) {
                    Log.d(TAG, "syncMeetings: update $meeting")
                    meeting.roomId = it.roomId
                    meeting.calendarId = it.calendarId
                    CalendarProvider.updateMeeting(getApplication(), meeting)
                    meetingRepository.updateMeeting(meeting)
                }
            }
            if (matchingMeetings.isNotEmpty()) {
                checkedMeetings.add(meeting)
                savedMeetings.removeAll(matchingMeetings.toSet())
            }
        }
        // Remove the meetings that are already saved and insert the new ones
        meetings.removeAll(checkedMeetings.toSet())
        meetings.forEach {
            Log.d(TAG, "syncMeetings: insert $it")
            it.calendarId = CalendarProvider.insertMeeting(getApplication(), it)
            meetingRepository.insertMeeting(it)
        }
        // Delete the meetings that are not in the remote database
        savedMeetings.forEach {
            CalendarProvider.deleteMeeting(getApplication(), it)
        }
        meetingRepository.deleteMeetings(savedMeetings)
    }

    private suspend fun syncRecurringMeetings(meetings: ArrayList<RecurringMeeting>) {
        val savedMeetings = ArrayList(recurringMeetingRepository.getAllRecurringMeetings())
        savedMeetings.forEach {
            Log.d(TAG, "syncRecurringMeetings: saved: $it")
        }
        val checkedMeetings = ArrayList<RecurringMeeting>()
        meetings.forEach { meeting ->
            val matchingMeetings = savedMeetings.filter { it.id == meeting.id }
            matchingMeetings.forEach {
                if (meeting != it) {
                    Log.d(TAG, "syncRecurringMeetings: update $meeting")
                    // Brute force calendar update because fuck it
                    CalendarProvider.deleteRecurringMeeting(getApplication(), it)
                    CalendarProvider.insertRecurringMeeting(getApplication(), meeting)
                    meeting.roomId = it.roomId
                    recurringMeetingRepository.updateRecurringMeeting(meeting)
                }
            }
            if (matchingMeetings.isNotEmpty()) {
                checkedMeetings.add(meeting)
                savedMeetings.removeAll(matchingMeetings.toSet())
            }
        }
        meetings.removeAll(checkedMeetings.toSet())
        meetings.forEach {
            Log.d(TAG, "syncRecurringMeetings: insert $it")
            CalendarProvider.insertRecurringMeeting(getApplication(), it)
            recurringMeetingRepository.insertRecurringMeeting(it)
        }
        savedMeetings.forEach {
            CalendarProvider.deleteRecurringMeeting(getApplication(), it)
        }
        recurringMeetingRepository.deleteRecurringMeetings(savedMeetings)
    }

    sealed class MainEvent {
        object CalendarPermissionEvent : MainEvent()
    }
}