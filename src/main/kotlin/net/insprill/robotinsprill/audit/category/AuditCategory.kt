package net.insprill.robotinsprill.audit.category

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.on
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.AuditColor
import net.insprill.robotinsprill.audit.AuditManager

sealed class AuditCategory(val robot: RobotInsprill, private val audit: AuditManager) {

    abstract fun registerEvents()

    suspend fun send(guildId: Snowflake?, user: User?, color: AuditColor, title: String, description: String? = null) {
        if (guildId == null) {
            robot.logger.debug { "Tried to send audit message with no guildId." }
            return
        }
        if (user == null) {
            robot.logger.debug { "Tried to send audit message with no user." }
            return
        }
        audit.sendMessage(guildId, user, color, title, description)
    }

    inline fun <reified T : Event> event(
        enabled: Boolean,
        noinline consumer: suspend T.() -> Unit
    ) {
        if (!enabled) return
        robot.kord.on(robot.kord, consumer)
    }

}
