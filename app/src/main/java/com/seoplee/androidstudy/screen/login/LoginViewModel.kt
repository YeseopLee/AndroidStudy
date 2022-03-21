package com.seoplee.androidstudy.screen.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seoplee.androidstudy.data.entity.user.User
import com.seoplee.androidstudy.data.repository.user.UserRepository
import com.seoplee.androidstudy.screen.main.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Uninitialized)
    val loginState : StateFlow<LoginState> get() = _loginState

    val userId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val userPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun signUp() {
        if(checkInput()) {
            if(userRepository.checkId(userId.value!!)) _loginState.value = LoginState.AlreadyExist
            else {
                userRepository.insertUser(User(
                    userId = userId.value!!,
                    userPassword = userPassword.value!!
                ))
            }
        }
    }

    fun signIn() {
        if(checkInput()) {
            if(userRepository.checkPassword(userId.value!!, userPassword.value!!)) {
                _loginState.value = LoginState.Success(
                    User(
                        userId = userId.value!!,
                        userPassword = userPassword.value!!
                    ))
            } else {
                _loginState.value = LoginState.PasswordError
            }
        }
    }

    private fun checkInput() : Boolean {
        when {
            userId.value.isNullOrEmpty() -> {
                _loginState.value = LoginState.NoId
                return false
            }
            userPassword.value.isNullOrEmpty() -> {
                _loginState.value = LoginState.NoPassword
                return false
            }
            // 정규식 추가 가능
        }
        return true
    }

    fun suspendExample() = viewModelScope.launch {

        val task1 : Deferred<String> = async {
            var i = 0
            while (i < 4) {
                println(i)
                delay(300)
                i++
            }
            "task1 done"
        }

        val task2 : Deferred<String> = async {
            var i = 0
            while (i < 6) {
                println(i)
                delay(300)
                i++
            }
            "task2 done"
        }

        val msg = awaitAll(task1, task2)
        println(msg)
    }
}