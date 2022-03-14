package com.seoplee.androidstudy.screen.login

import com.seoplee.androidstudy.data.entity.user.User

sealed class LoginState{

    object Uninitialized: LoginState()

    data class Success(
        val userInfo: User
    ) : LoginState()

    object NoId : LoginState()
    object NoPassword : LoginState()
    object AlreadyExist: LoginState()
    object PasswordError : LoginState()
}