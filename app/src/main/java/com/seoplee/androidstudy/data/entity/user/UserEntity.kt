package com.seoplee.androidstudy.data.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    val userId: String,
    val userPassword: String
)