package com.seoplee.androidstudy.data.repository.passenger

import androidx.paging.PagingData
import com.seoplee.androidstudy.data.entity.NetworkResult
import com.seoplee.androidstudy.data.entity.passenger.Data
import com.seoplee.androidstudy.data.entity.passenger.Passenger
import kotlinx.coroutines.flow.Flow

interface PassengerRepository {

    suspend fun getPassengers() : NetworkResult<Passenger>

    fun getPagingData() : Flow<PagingData<Data>>
}