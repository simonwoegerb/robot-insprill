package net.insprill.robotinsprill.social

import com.github.kittinunf.fuel.Fuel
import kotlinx.serialization.Serializable
import net.insprill.robotinsprill.extension.awaitObjectKResult
import net.insprill.robotinsprill.extension.urlEncoded

object YoutubeApi {

    suspend fun getChannels(channelId: String): Result<Channels> {
        val encodedChannelId = channelId.urlEncoded()
        val encodedApiKey = System.getenv("YOUTUBE_API_KEY").urlEncoded()
        return Fuel.get("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=$encodedChannelId&key=$encodedApiKey")
            .awaitObjectKResult()
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
