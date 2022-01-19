package com.seoplee.androidstudy.data.repository.user

import androidx.room.Room
import com.seoplee.androidstudy.MyApp.Companion.appContext
import com.seoplee.androidstudy.data.entity.user.UserEntity
import com.seoplee.androidstudy.data.room.AppDataBase
import javax.inject.Inject
import javax.inject.Singleton

class DefaultUserRepository @Inject constructor(private val db: AppDataBase) : UserRepository {

    override fun insertUser(userEntity: UserEntity) {
        db.UserDao().insertUserInfo(userEntity)
    }

    override fun checkPassword(userId: String, userPassword: String): Boolean {
        val password = db.UserDao().getPassword(userId)
        return password == userPassword
    }

    override fun checkId(userId: String): Boolean {
        val userList = db.UserDao().getIds()

        userList.forEach { id ->
            if (id == userId) return true
        }

        return false
    }
}