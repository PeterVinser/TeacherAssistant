package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.user.User

sealed class SearchedUserItem {

    abstract val id: String

    data class UserHint(val userId: String, val fullName: String): SearchedUserItem() {
        override val id = userId
    }

    data class UserProfile(val user: User): SearchedUserItem() {
        override val id = user.userId
    }
}