package com.piotrokninski.teacherassistant.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.contract.room.RoomUserContract

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User): Int

    @Query(
        "SELECT * FROM ${RoomUserContract.TABLE_NAME} " +
                "WHERE ${RoomUserContract.USER_ID} = :userId" +
                " LIMIT 1"
    )
    fun getLiveUser(userId: String): LiveData<User>

    @Query(
        "SELECT * FROM ${RoomUserContract.TABLE_NAME} " +
                "WHERE ${RoomUserContract.USER_ID} = :userId" +
                " LIMIT 1"
    )
    suspend fun getUser(userId: String): User?
}