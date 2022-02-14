package com.seoplee.androidstudy.screen.main

import com.seoplee.androidstudy.data.entity.passenger.Data
import com.seoplee.androidstudy.data.entity.passenger.Passenger
import com.seoplee.androidstudy.data.entity.user.UserEntity

sealed class MainState{

    object Uninitialized: MainState()

    data class Success(
        val passengerInfo: Passenger
    ) : MainState()

    data class Error(
        val code : String?
    ) : MainState()

}