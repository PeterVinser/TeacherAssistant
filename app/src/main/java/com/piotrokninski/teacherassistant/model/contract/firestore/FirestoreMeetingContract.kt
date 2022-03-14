package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreMeetingContract {

    const val COLLECTION_NAME = "meetings"

    const val COURSE_ID = "courseId"

    const val LESSON_ID = "lessonId"

    const val STUDENT_ID = "studentId"
    const val STUDENT_FULL_NAME = "studentFullName"

    const val TUTOR_ID = "tutorId"
    const val TUTOR_FULL_NAME = "tutorFullName"

    const val SUBJECT = "subject"

    const val DATE = "date"

    const val STATUS = "status"

    const val STATUS_RECURRING = "recurring"
    const val STATUS_UPCOMING = "upcoming"
    const val STATUS_CANCELED = "canceled"

    const val DESCRIPTION = "description"

    const val MEETING_DATES = "meetingDates"

    const val MEETING_DURATION = "meetingDuration"
}