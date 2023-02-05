package net.insprill.robotinsprill.extension

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResult
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import com.github.kittinunf.result.Result
import kotlinx.serialization.json.Json

suspend inline fun <reified U : Any> Request.awaitObjectKResult(): kotlin.Result<U> =
    awaitResult(kotlinxDeserializerOf<U>(Json { ignoreUnknownKeys = true })).kotlin()


suspend inline fun Request.awaitStringKResult(): kotlin.Result<String> =
    awaitStringResult().kotlin()

suspend inline fun Request.awaitByteArrayKResult(): kotlin.Result<ByteArray> =
    awaitByteArrayResult().kotlin()

fun <T, E : Exception> Result<T, E>.kotlin(): kotlin.Result<T> {
    return this.fold({ kotlin.Result.success(it) }, { kotlin.Result.failure(it) })
}
