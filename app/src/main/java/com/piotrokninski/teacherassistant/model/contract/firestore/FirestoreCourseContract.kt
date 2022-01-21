package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreCourseContract {

    const val COLLECTION_NAME = "courses"

    const val COURSE_ID = "courseId"

    const val STUDENT_ID = "studentId"
    const val TUTOR_ID = "tutorId"

    const val STUDENT_FULL_NAME = "studentFullName"
    const val TUTOR_FULL_NAME = "tutorFullName"

    const val STATUS = "status"
    const val STATUS_PENDING = "pending"
    const val STATUS_APPROVED = "approved"

    const val COURSE_TYPE = "type"
    const val TYPE_REMOTE = "remote"
    const val TYPE_STATIONARY = "stationary"

    const val SUBJECT = "subject"

    const val MEETINGS_DATES = "meetingsDates"
}