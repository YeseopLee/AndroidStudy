package com.seoplee.androidstudy.util

import android.util.Log
import com.seoplee.androidstudy.data.network.ServerApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.seoplee.androidstudy.data.entity.passenger.Data
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class SamplePagingSource(
    private val serverApi: ServerApi
) : PagingSource<Int, Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Data> {
        return try {
            val next = params.key ?: 0
            val response = serverApi.getPassengers(next, 10)
            LoadResult.Page(
                data = response.body()!!.data,
                prevKey = if (next == 0) null else next - 1,
                nextKey = next + 1

            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
