package com.seoplee.androidstudy.data.repository.passenger

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.seoplee.androidstudy.util.SamplePagingSource
import com.seoplee.androidstudy.data.entity.NetworkResult
import com.seoplee.androidstudy.data.entity.passenger.Data
import com.seoplee.androidstudy.data.entity.passenger.Passenger
import com.seoplee.androidstudy.data.network.ServerApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultPassengerRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val ioDispatcher: CoroutineDispatcher
) : PassengerRepository {
    override suspend fun getPassengers(): NetworkResult<Passenger> = withContext(ioDispatcher) {
        val response = serverApi.getPassengers(0, 10)

        if(response.isSuccessful) {
            NetworkResult.success(
                data = response.body()!!
            )
        } else {
            NetworkResult.error(
                code = "400"
            )
        }
    }

    override fun getPagingData(): Flow<PagingData<Data>>  {

        val flow = Pager(
            PagingConfig(pageSize = 10)
        ) {
            SamplePagingSource(serverApi)
        }.flow

        return flow
    }

}