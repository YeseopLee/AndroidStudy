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
        try {
            // Start refresh at page 1 if undefined.
            val next = params.key ?: 0
            val response = serverApi.getPassengers(next, 10)
            val response2 = listOf<Data>(
                Data(
                    _id ="1",
                    name="1",
                    trips = 200,
                    airline = listOf(),
                    __v = 3
                )
            )
            Log.e("response Log",response.body()!!.data.toString())
            return LoadResult.Page(
                data = response2,
                prevKey = if (next == 0) null else next - 1,
                nextKey = next + 1

            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
