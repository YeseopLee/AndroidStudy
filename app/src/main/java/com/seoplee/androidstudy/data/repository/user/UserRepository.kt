package com.seoplee.androidstudy.data.repository.user

import com.seoplee.androidstudy.data.entity.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun insertUser(user: User)

    fun checkPassword(userId: String, userPassword: String): Boolean

    fun checkId(userId: String): Boolean

    fun getAllUsers(): Flow<List<User>>

    fun deleteUser(user: User): Boolean

}