package net.insprill.robotinsprill.extension

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import dev.kord.core.entity.Attachment

suspend fun Attachment.stringContent(): Result<String> {
    Fuel.get(this.url).awaitStringResult().fold(
        { content -> return Result.success(content) },
        { err -> return Result.failure(err) }
    )
}