package com.seoplee.androidstudy.screen.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seoplee.androidstudy.data.entity.user.UserEntity
import com.seoplee.androidstudy.data.repository.user.DefaultUserRepository

class LoginViewModel : ViewModel() {

    private val userRepository by lazy {
        DefaultUserRepository()
    }
    val userId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val userPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val loginState : MutableLiveData<LoginState> by lazy {
        MutableLiveData<LoginState>(LoginState.Uninitialized)
    }

    fun signUp() {
        if(checkInput()) {
            if(userRepository.checkId(userId.value!!)) loginState.value = LoginState.AlreadyExist
            else {
                userRepository.insertUser(UserEntity(
                    userId = userId.value!!,
                    userPassword = userPassword.value!!
                ))
            }
        }
    }

    fun signIn() {
        if(checkInput()) {
            if(userRepository.checkPassword(userId.value!!, userPassword.value!!)) {
                loginState.value = LoginState.Success(
                    UserEntity(
                        userId = userId.value!!,
                        userPassword = userPassword.value!!
                    ))
            } else {
                loginState.value = LoginState.PasswordError
            }
        }
    }

    private fun checkInput() : Boolean {
        when {
            userId.value.isNullOrEmpty() -> {
                loginState.value = LoginState.NoId
                return false
            }
            userPassword.value.isNullOrEmpty() -> {
                loginState.value = LoginState.NoPassword
                return false
            }
            // 정규식 추가 가능
        }
        return true
    }
}