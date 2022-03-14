package com.seoplee.androidstudy.data.room.dao

import androidx.room.*
import com.seoplee.androidstudy.data.entity.todo.Todo
import com.seoplee.androidstudy.data.entity.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM Todo")
    fun getTodos(): Flow<List<Todo>>



}