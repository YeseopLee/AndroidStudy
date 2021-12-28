package com.seoplee.androidstudy.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.seoplee.androidstudy.data.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    fun insertUserInfo(userEntity: UserEntity)

    @Query("SELECT userPassword FROM UserEntity WHERE userId = :userId")
    fun getUserPassword(userId: String): String

    @Query("SELECT userId FROM UserEntity")
    fun getUserId(): UserEntity?

}