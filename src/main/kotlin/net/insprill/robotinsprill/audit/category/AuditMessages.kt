package net.insprill.robotinsprill.audit.category

import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.event.message.MessageUpdateEvent
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.AuditColor
import net.insprill.robotinsprill.audit.AuditManager

class AuditMessages(robot: RobotInsprill, audit: AuditManager) : AuditCategory(robot, audit) {

    override fun registerEvents() {
        val config = robot.config.audit.events.messages
        event<MessageDeleteEvent>(config.deleted) {
            send(
                guildId,
                message?.author,
                AuditColor.RED,
                "Message sent by <@${message?.author?.id?.value}> deleted in <#${channelId.value}>.",
                message?.content
            )
        }
        event<MessageUpdateEvent>(config.edited) {
            send(
                getMessage().getGuild().id,
                getMessage().author,
                AuditColor.ORANGE,
                "Message sent by <@${getMessage().author?.id?.value}> edited in <#${channelId.value}>.",
                """
                **Old Message**
                ${old?.content}

                **New Message**
                ${new.content.value}
                """.trim()
            )
        }
    }

}
