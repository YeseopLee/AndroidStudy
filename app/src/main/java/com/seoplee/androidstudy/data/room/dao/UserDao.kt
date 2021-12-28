package com.seoplee.androidstudy.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seoplee.androidstudy.data.entity.user.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(userEntity: UserEntity)

    @Query("SELECT userPassword FROM UserEntity WHERE userId = :userId")
    fun getPassword(userId: String): String

    @Query("SELECT userId FROM UserEntity")
    fun getIds(): List<String>

}