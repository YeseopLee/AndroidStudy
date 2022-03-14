package com.seoplee.androidstudy.util

import com.seoplee.androidstudy.data.entity.todo.Todo

interface TodoAdapterListener {

    fun onDeleteItem(item: Todo)

}