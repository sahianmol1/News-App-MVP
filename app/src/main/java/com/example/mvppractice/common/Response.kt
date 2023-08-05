package com.example.mvppractice.common

sealed class Response<T>(data: T? = null) {
    object Loading: Response<Unit>()
    data class Success<T>(val data: T): Response<T>(data = data)
    data class Error(val error: String): Response<String>(data = error)
}
