package com.seoplee.androidstudy.data.network

import com.seoplee.androidstudy.data.entity.passenger.Passenger
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {

    @GET("v1/passenger")
    suspend fun getPassengers(@Query("page")page: Int, @Query("size")size: Int) : Response<Passenger>
}
