package com.seoplee.androidstudy.data.repository.user

import android.util.Log
import com.seoplee.androidstudy.data.entity.user.User
import com.seoplee.androidstudy.data.room.AppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(private val db: AppDataBase) : UserRepository {

    override fun insertUser(user: User) {
        db.UserDao().insertUserInfo(user)
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

    override fun getAllUsers() : Flow<List<User>>  {
        return db.UserDao().getUsers()
    }

    override fun deleteUser(user: User): Boolean {
        return true
    }

}