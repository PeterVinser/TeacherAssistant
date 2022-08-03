package com.piotrokninski.teacherassistant.viewmodel.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.UserHint
import com.piotrokninski.teacherassistant.repository.calendar.CalendarProvider
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomMeetingRepository
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(
    private val dataStoreRepository: DataStoreRepository, application: Application, initUser: User?
) : AndroidViewModel(application) {
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
            saveUser(initUser)

            saveViewType()

            eventChannel.send(MainEvent.CalendarPermissionEvent)
        }
    }

    private suspend fun saveUser(user: User?) {
        if (user != null) {

            val userHint = UserHint.createHint(user.userId, user.fullName)

            FirestoreUserRepository.setUserData(user)
            FirestoreUserHintRepository.setUserHintData(userHint)

            userRepository.insertUser(user)

            return
        }

        if (userRepository.getUser(currentUserId) == null) {
            val currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId)

            if (currentUser != null) {
                userRepository.insertUser(currentUser)
            } else {
                // TODO: add the logic in case saving the user goes wrong
            }
        }
    }

    private suspend fun saveViewType() {
        dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE).let { viewType ->
            if (viewType != null) {
                _viewType.value = viewType

                return
            }

            val currentUser = userRepository.getLiveUser(currentUserId).value

            if (currentUser != null) {
                if (currentUser.student) {
                    _viewType.value = AppConstants.VIEW_TYPE_STUDENT
                    dataStoreRepository
                        .putString(DataStoreRepository.Constants.VIEW_TYPE, AppConstants.VIEW_TYPE_STUDENT)

                    return
                }

                if (currentUser.tutor) {
                    _viewType.value = AppConstants.VIEW_TYPE_TUTOR
                    dataStoreRepository
                        .putString(DataStoreRepository.Constants.VIEW_TYPE, AppConstants.VIEW_TYPE_TUTOR)

                    return
                }
            }

            _viewType.value = AppConstants.VIEW_TYPE_STUDENT
            dataStoreRepository.putString(DataStoreRepository.Constants.VIEW_TYPE, AppConstants.VIEW_TYPE_STUDENT)
        }
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
                meeting.calendarId = CalendarProvider.insertMeeting(getApplication(), meeting)
                meetingRepository.insertMeeting(meeting)
                null
            }?.forEach { savedMeeting ->
                if (!equals(meeting, savedMeeting)) {
                    meeting.roomId = savedMeeting.roomId
                    meeting.calendarId = savedMeeting.calendarId
                    CalendarProvider.updateMeeting(getApplication(), meeting)
                    meetingRepository.updateMeeting(meeting)
                }

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
                that.durationMinutes == other.durationMinutes &&
                that.weekDate == other.weekDate

    sealed class MainEvent {
        object CalendarPermissionEvent : MainEvent()
    }

    class Factory(
        private val dataStoreRepository: DataStoreRepository,
        private val application: Application,
        private val user: User?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(dataStoreRepository, application, user) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}