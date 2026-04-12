package com.sukajee.nepalikeyboard.core.common.util

/**
 * Generic result wrapper used across all modules.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success<T>) block(data)
    return this
}

fun <T> Result<T>.onError(block: (String, Throwable?) -> Unit): Result<T> {
    if (this is Result.Error) block(message, throwable)
    return this
}
