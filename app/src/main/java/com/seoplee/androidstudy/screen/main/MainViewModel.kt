package com.seoplee.androidstudy.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.data.repository.todo.TodoRepository
import com.seoplee.androidstudy.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    val mainStateLiveData = MutableLiveData<MainState>(MainState.Uninitialized)

    val todo: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getAllTodos() = viewModelScope.launch {
        mainStateLiveData.value = MainState.Loading

        val response = todoRepository.getAllTodos()

        mainStateLiveData.value = MainState.GetSuccess(response)
    }

    fun addTodo() = viewModelScope.launch {
        mainStateLiveData.value = MainState.Loading

        todo.value?.let {
            todoRepository.insertTodo(Todo(description = it))
            mainStateLiveData.value = MainState.AddSuccess
        } ?: run {
            mainStateLiveData.value = MainState.Error("에러")
        }

    }

    fun deleteTodo(delTodo: Todo) = viewModelScope.launch {
        mainStateLiveData.value = MainState.Loading

        todoRepository.deleteTodo(delTodo)

        mainStateLiveData.value = MainState.DeleteSuccess
    }

}