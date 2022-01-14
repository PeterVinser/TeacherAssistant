package com.piotrokninski.teacherassistant.model

import com.piotrokninski.teacherassistant.model.user.User

sealed class SearchedUserItem {

    abstract val id: String

    data class UserHint(var userId: String, var fullName: String): SearchedUserItem() {
        override val id = userId
    }

    data class UserProfile(var user: User): SearchedUserItem() {
        override val id = user.userId
    }
}