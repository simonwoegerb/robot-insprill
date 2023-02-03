package net.insprill.robotinsprill.audit.category

import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.on
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.AuditColor
import net.insprill.robotinsprill.audit.AuditManager

sealed class AuditCategory(val robot: RobotInsprill, private val audit: AuditManager) {

    abstract fun registerEvents()

    suspend fun send(user: User?, color: AuditColor, title: String, description: String? = null) {
        if (user == null) {
            robot.logger.debug { "Tried to send audit message with no user." }
            return
        }
        audit.sendUserMessage(user, color, title, description)
    }

    suspend fun send(color: AuditColor, title: String, footer: String? = null) {
        audit.sendServerMessage(color, title, footer)
    }

    inline fun <reified T : Event> event(
        enabled: Boolean,
        noinline consumer: suspend T.() -> Unit
    ) {
        if (!enabled) return
        robot.kord.on(robot.kord, consumer)
    }

}
