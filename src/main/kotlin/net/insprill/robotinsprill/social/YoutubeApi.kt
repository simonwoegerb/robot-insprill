package net.insprill.robotinsprill.social

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import com.github.kittinunf.result.Result
import java.net.URLEncoder
import kotlinx.serialization.Serializable

object YoutubeApi {

    suspend fun getChannels(channelId: String): Result<Channels, FuelError> {
        val encodedChannelId = URLEncoder.encode(channelId, Charsets.UTF_8)
        val encodedApiKey = URLEncoder.encode(System.getenv("YOUTUBE_API_KEY"), Charsets.UTF_8)
        return Fuel.get("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=$encodedChannelId&key=$encodedApiKey")
            .awaitObjectResponseResult<Channels>(kotlinxDeserializerOf()).third
    }

    @Serializable
    data class Channels(val items: List<Channel>) {
        @Serializable
        data class Channel(val statistics: Statistics) {
            @Serializable
            data class Statistics(val subscriberCount: Long, val viewCount: Long)
        }
    }
}
