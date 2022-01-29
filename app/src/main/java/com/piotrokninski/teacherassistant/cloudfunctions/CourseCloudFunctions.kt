package com.piotrokninski.teacherassistant.cloudfunctions

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.util.Util.serializeToMap

object CourseCloudFunctions {
    private const val TAG = "CourseCloudFunctions"

    private val functions = FirebaseFunctions.getInstance("europe-west1")

    fun addCourse(course: Course) {

        val courseData = course.serializeToMap()

        functions
            .getHttpsCallable("course-addCourse")
            .call(courseData)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "addCourse: ", e)
                }
            }
    }

    fun confirmCourse(course: Course) {

        val courseData = course.serializeToMap()

        functions
            .getHttpsCallable("course-approveCourse")
            .call(courseData)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "confirmCourse: ", e)
                }
            }
    }
}