package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreHomeworkContract {

    const val COLLECTION_NAME = "homeworks"

    const val COURSE_ID = "courseId"
    const val LESSON_ID = "lessonId"

    const val STUDENT_ID = "studentId"
    const val STUDENT_FULL_NAME = "studentFullName"

    const val TUTOR_ID = "tutorId"
    const val TUTOR_FULL_NAME = "tutorFullName"

    const val TOPIC = "topic"
    const val SUBJECT = "subject"

    const val CREATION_DATE = "creationDate"
    const val DUE_DATE = "dueDate"

    const val STATUS = "status"

    const val STATUS_ASSIGNED = "assigned"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_UNDELIVERED = "undelivered"

    const val DESCRIPTION = "description"
}