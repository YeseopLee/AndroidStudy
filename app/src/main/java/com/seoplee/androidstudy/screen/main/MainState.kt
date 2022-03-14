package com.seoplee.androidstudy.screen.main

import androidx.lifecycle.LiveData
import com.seoplee.androidstudy.data.entity.todo.Todo
import kotlinx.coroutines.flow.Flow

sealed class MainState{

    object Uninitialized: MainState()

    object Loading: MainState()

    data class GetSuccess(
        val todoInfo: Flow<List<Todo>>
    ) : MainState()

    object DeleteSuccess : MainState()

    object AddSuccess : MainState()

    data class Error(
        val message : String?
    ) : MainState()

}