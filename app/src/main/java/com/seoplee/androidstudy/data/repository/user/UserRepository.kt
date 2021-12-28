package com.seoplee.androidstudy.data.repository.user

import com.seoplee.androidstudy.data.entity.user.UserEntity

interface UserRepository {

    fun insertUser(userEntity: UserEntity)

    fun checkPassword(userId: String, userPassword: String): Boolean

    fun checkId(userId: String): Boolean

}