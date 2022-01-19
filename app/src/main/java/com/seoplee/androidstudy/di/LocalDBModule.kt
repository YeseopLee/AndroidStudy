package com.seoplee.androidstudy.di

import androidx.room.Room
import com.seoplee.androidstudy.MyApp
import com.seoplee.androidstudy.data.room.AppDataBase
import com.seoplee.androidstudy.data.room.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDBModule{

    @Provides
    @Singleton
    fun provideLocalDB() : AppDataBase = Room
        .databaseBuilder(MyApp.appContext!!, AppDataBase::class.java, AppDataBase.DB_NAME)
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideUserDao(appDataBase: AppDataBase): UserDao = appDataBase.UserDao()

}