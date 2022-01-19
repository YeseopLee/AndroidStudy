package com.seoplee.androidstudy.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seoplee.androidstudy.data.entity.user.UserEntity
import com.seoplee.androidstudy.data.room.dao.UserDao

@Database(
    entities =[UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        const val DB_NAME = "AppDataBase.db"
    }

    abstract fun UserDao(): UserDao
}
