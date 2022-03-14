package com.seoplee.androidstudy.data.repository.todo

import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.data.repository.user.UserRepository
import com.seoplee.androidstudy.data.room.AppDataBase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultTodoRepository @Inject constructor(private val db: AppDataBase) : TodoRepository {

    override fun insertTodo(todo: Todo) {
        db.TodoDao().insertTodo(todo)
    }

    override fun deleteTodo(todo: Todo) {
        db.TodoDao().deleteTodo(todo)
    }

    override fun getAllTodos(): Flow<List<Todo>> {
        return db.TodoDao().getTodos()
    }

}