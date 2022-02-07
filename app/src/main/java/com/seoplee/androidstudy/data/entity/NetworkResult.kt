package com.seoplee.androidstudy.data.entity

data class NetworkResult<out T>(val status: Status, val data: T?, val message: String?, val code: String?) {

    enum class Status { SUCCESS, ERROR }

    companion object {
        fun <T> success(data: T, message: String? = null, code: String? = null): NetworkResult<T> {
            return NetworkResult(Status.SUCCESS, data, message, code)
        }

        fun <T> error(message: String? = null, data: T? = null, code: String?): NetworkResult<T> {
            return NetworkResult(Status.ERROR, data, message, code)
        }
    }
}