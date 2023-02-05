package net.insprill.robotinsprill.extension

import com.github.kittinunf.fuel.Fuel
import dev.kord.core.entity.Attachment

suspend fun Attachment.stringContent(): Result<String> {
    Fuel.get(this.url).awaitStringKResult().fold(
        { content -> return Result.success(content) },
        { err -> return Result.failure(err) }
    )
}

suspend fun Attachment.byteContent(): Result<ByteArray> {
    Fuel.get(this.url).awaitByteArrayKResult().fold(
        { content -> return Result.success(content) },
        { err -> return Result.failure(err) }
    )
}
