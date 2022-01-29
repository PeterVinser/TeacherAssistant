package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.user.User

sealed class SearchUserAdapterItem {

    abstract val id: String

    data class UserAdapterHint(val userId: String, val fullName: String): SearchUserAdapterItem() {
        override val id = userId
    }

    data class UserAdapterProfile(val user: User): SearchUserAdapterItem() {
        override val id = user.userId
    }
}