package net.insprill.robotinsprill.audit

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.category.AuditMembers
import net.insprill.robotinsprill.audit.category.AuditMessages
import net.insprill.robotinsprill.audit.category.AuditServer

class AuditManager(private val robot: RobotInsprill) {

    private val channelCache: MutableMap<Snowflake, MessageChannel> = HashMap()

    fun setupEventHandlers() {
        arrayOf(
            AuditMembers(robot, this),
            AuditMessages(robot, this),
            AuditServer(robot, this),
        ).forEach { it.registerEvents() }
    }

    suspend fun sendUserMessage(
        user: User,
        color: AuditColor,
        title: String,
        description: String?
    ) {
        if (!robot.config.audit.logBots && user.isBot) return
        val embed = buildUserEmbed(user, color, title, description)
        sendMessage(embed)
    }

    suspend fun sendServerMessage(
        color: AuditColor,
        title: String,
        footer: String?,
    ) {
        val embed = buildServerEmbed(color, title, footer)
        sendMessage(embed)
    }

    private suspend fun sendMessage(embed: EmbedBuilder.() -> Unit) {
        channelCache.getOrPut(robot.config.guildId) {
            robot.kord.getGuildOrThrow(robot.config.guildId)
                .getChannel(robot.config.audit.auditChannel) as MessageChannel
        }.createEmbed(embed)
    }

    private fun buildUserEmbed(
        user: User,
        color: AuditColor,
        title: String,
        description: String?
    ): EmbedBuilder.() -> Unit {
        val footer = "User ID: ${user.id.value} • <t:${System.currentTimeMillis() / 1000}:f>"
        return {
            author {
                name = user.tag
                icon = user.avatar?.url
            }
            this.color = color.color
            this.description = "**$title**\n\n${if (description != null) "$description\n\n" else ""}$footer"

        }
    }

    private fun buildServerEmbed(
        color: AuditColor,
        title: String,
        footer: String?,
    ): EmbedBuilder.() -> Unit {
        val finalFooter = "${if (footer != null) "$footer • " else ""}<t:${System.currentTimeMillis() / 1000}:f>"
        return {
            this.color = color.color
            this.description = "**$title**\n\n$finalFooter"
        }
    }

}
