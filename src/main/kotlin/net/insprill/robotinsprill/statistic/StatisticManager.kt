package net.insprill.robotinsprill.statistic

import dev.kord.core.behavior.channel.edit
import dev.kord.core.entity.channel.VoiceChannel
import kotlin.concurrent.timer
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import net.insprill.robotinsprill.RobotInsprill

class StatisticManager(private val robot: RobotInsprill) {

    suspend fun start(period: Long) = coroutineScope {
        timer(daemon = true, period = period) {
            val scope = robot.kord + SupervisorJob(robot.kord.coroutineContext.job)
            scope.launch {
                update()
            }
        }
    }

    private suspend fun update() {
        robot.logger.info("Updating statistic channels")
        robot.config.statisticChannels.forEach {
            val guild = robot.kord.getGuildOrNull(it.channelId.first)
            if (guild == null) {
                robot.logger.error("Failed to find guild ${it.channelId.first}")
                return@forEach
            }

            val channel = guild.getChannelOrNull(it.channelId.second)
            if (channel == null) {
                robot.logger.error("Failed to find channel ${it.channelId.first} in guild ${it.channelId.first}")
                return@forEach
            }
            if (channel !is VoiceChannel) {
                robot.logger.error("Channel ${it.channelId.first} in guild ${it.channelId.first} isn't a voice channel!")
                return@forEach
            }

            channel.edit {
                name = it.format.format(it.statistic.func(robot, guild, it.data))
            }
        }
    }

}
