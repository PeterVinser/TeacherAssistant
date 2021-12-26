package com.piotrokninski.teacherassistant.repository.room.repository

import androidx.lifecycle.LiveData
import com.piotrokninski.teacherassistant.model.User
import com.piotrokninski.teacherassistant.repository.room.dao.UserDAO

class RoomUserRepository(private val dao: UserDAO) {

    suspend fun insertUser(user: User): Long {
        return dao.insertUser(user)
    }

    suspend fun updateUser(user: User): Int {
        return dao.updateUser(user)
    }

    suspend fun deleteUser(user: User): Int {
        return dao.deleteUser(user)
    }

    fun getUser(userId: String): LiveData<User> {
        return dao.getUser(userId)
    }
}