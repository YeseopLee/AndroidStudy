package com.seoplee.androidstudy.screen.main

import android.util.Log
import androidx.lifecycle.*
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.data.repository.todo.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainState>(MainState.Uninitialized)
    val uiState : StateFlow<MainState> get() = _uiState

    val todo: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // ui layer에서 repeatOnLifecycle을 이용하여 리소스 낭비 방지
    fun getAllTodosWithCollect() = viewModelScope.launch {
        _uiState.value = MainState.Loading

        val response = todoRepository.getAllTodos()

        _uiState.value = MainState.GetSuccess(response)
    }

    fun getAllTodosWithoutLifeCycle() : Flow<List<Todo>> {
        return todoRepository.getAllTodos()
    }

    fun addTodo() = viewModelScope.launch {
        _uiState.value = MainState.Loading

        todo.value?.let {
            todoRepository.insertTodo(Todo(description = it))
            _uiState.value = MainState.AddSuccess
        } ?: run {
            _uiState.value = MainState.Error("에러")
        }

    }

    fun deleteTodo(delTodo: Todo) = viewModelScope.launch {
        _uiState.value = MainState.Loading

        todoRepository.deleteTodo(delTodo)

        _uiState.value = MainState.DeleteSuccess
    }

}