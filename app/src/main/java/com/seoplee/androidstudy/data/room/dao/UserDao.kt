package com.seoplee.androidstudy.data.room.dao

import androidx.room.*
import com.seoplee.androidstudy.data.entity.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT userPassword FROM User WHERE userId = :userId")
    fun getPassword(userId: String): String

    @Query("SELECT userId FROM User")
    fun getIds(): List<String>

    @Query("SELECT * FROM User")
    fun getUsers(): Flow<List<User>>



}