package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.course.Homework

sealed class HomeworkAdapterItem {

    abstract val id: String

    data class HomeworkItem(val homework: Homework) : HomeworkAdapterItem() {
        override val id = homework.toString()
    }

    data class HeaderItem(val titleId: Int) : HomeworkAdapterItem() {
        override val id = titleId.toString()
    }
}
