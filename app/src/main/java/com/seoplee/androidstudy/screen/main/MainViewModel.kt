package com.seoplee.androidstudy.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.seoplee.androidstudy.data.entity.NetworkResult
import com.seoplee.androidstudy.data.entity.passenger.Data
import com.seoplee.androidstudy.data.network.ServerApi
import com.seoplee.androidstudy.data.repository.passenger.PassengerRepository
import com.seoplee.androidstudy.util.SamplePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val passengerRepository: PassengerRepository,
private val serverApi: ServerApi) : ViewModel() {

    private val mainStateLiveData = MutableLiveData<MainState>(MainState.Uninitialized)

    fun getPagingData() : Flow<PagingData<Data>> {
        return passengerRepository.getPagingData().cachedIn(viewModelScope)
    }


    fun fetchData() = viewModelScope.launch {
        val response = passengerRepository.getPassengers()

//        val pagingData = passengerRepository.getPagingData().cachedIn(viewModelScope)

        if(response.status == NetworkResult.Status.SUCCESS) {
            Log.e("dataTest",response.data.toString())
        } else {
            Log.e("error","error")
        }

//        Log.e("pagingData", pagingData.collect().toString())
    }
}