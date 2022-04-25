package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.course.Course

sealed class CourseAdapterItem {

    abstract val id: String

    data class CourseItem(val course: Course) : CourseAdapterItem() {
        override val id = course.courseId ?: course.tutorId
    }

    data class HeaderItem(val titleId: Int) : CourseAdapterItem() {
        override val id = titleId.toString()
    }
}
