package com.seoplee.androidstudy.data.repository.todo

import com.seoplee.androidstudy.data.entity.todo.Todo
import kotlinx.coroutines.flow.Flow


interface TodoRepository {

    fun insertTodo(todo: Todo)

    fun deleteTodo(todo: Todo)

    fun getAllTodos(): Flow<List<Todo>>

}