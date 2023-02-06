package net.insprill.robotinsprill.statistic

import dev.kord.core.entity.Guild
import io.ktor.util.logging.error
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.extension.pretty
import net.insprill.robotinsprill.social.YoutubeApi

@Suppress("UNUSED")
enum class Statistic(val func: suspend (RobotInsprill, Guild, String?) -> String) {
    YOUTUBE_SUBS({ robot, _, data ->
        YoutubeApi.getChannels(data!!)
            .map { it.items.first().statistics.subscriberCount.pretty() }
            .onFailure { robot.logger.error(it) }
            .getOrNull() ?: "Error"
    }),
    YOUTUBE_VIEWS({ robot, _, data ->
        YoutubeApi.getChannels(data!!)
            .map { it.items.first().statistics.viewCount.pretty() }
            .onFailure { robot.logger.error(it) }
            .getOrNull() ?: "Error"
    }),
    ;
}
