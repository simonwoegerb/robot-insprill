package net.insprill.robotinsprill.audit

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.category.AuditMembers
import net.insprill.robotinsprill.audit.category.AuditMessages

class AuditManager(private val robot: RobotInsprill) {

    private val channelCache: MutableMap<ULong, MessageChannel> = HashMap()

    fun setupEventHandlers() {
        arrayOf(
            AuditMembers(robot, this),
            AuditMessages(robot, this),
        ).forEach { it.registerEvents() }
    }

    suspend fun sendMessage(
        guildId: Snowflake,
        user: User,
        color: AuditColor,
        title: String,
        description: String? = null
    ) {
        val embed = buildEmbed(user, color, title, description)
        if (channelCache.contains(guildId.value)) {
            channelCache[guildId.value]?.createEmbed(embed)
        } else {
            val channel = robot.kord.getGuildOrThrow(guildId)
                .getChannel(Snowflake(robot.config.audit.channelIds[guildId.value]!!)) as MessageChannel
            channelCache[guildId.value] = channel
            channel.createEmbed(embed)
        }
    }

    private fun buildEmbed(
        user: User,
        color: AuditColor,
        title: String,
        description: String? = null
    ): EmbedBuilder.() -> Unit {
        return {
            author {
                name = user.tag
                icon = user.avatar?.url
            }
            this.color = color.color
            this.description = """
                **$title**

                $description

                User ID: ${user.id.value} • <t:${System.currentTimeMillis() / 1000}:f>
            """.trim()
        }
    }

}
