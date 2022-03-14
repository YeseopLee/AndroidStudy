package com.seoplee.androidstudy.data.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val userId: String,
    val userPassword: String
)