package com.seoplee.androidstudy.data.entity.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val description: String
)