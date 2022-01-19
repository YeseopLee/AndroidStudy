package com.seoplee.androidstudy.di

import com.seoplee.androidstudy.data.repository.user.DefaultUserRepository
import com.seoplee.androidstudy.data.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        userRepository: DefaultUserRepository
    ): UserRepository
}