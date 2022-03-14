package com.seoplee.androidstudy.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.data.entity.user.User
import com.seoplee.androidstudy.data.room.dao.TodoDao
import com.seoplee.androidstudy.data.room.dao.UserDao

@Database(
    entities =[User::class, Todo::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        const val DB_NAME = "AppDataBase.db"
    }

    abstract fun UserDao(): UserDao
    abstract fun TodoDao(): TodoDao
}
