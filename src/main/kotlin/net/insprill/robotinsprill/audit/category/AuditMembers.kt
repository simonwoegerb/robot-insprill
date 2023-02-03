package net.insprill.robotinsprill.audit.category

import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.BanRemoveEvent
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.audit.AuditColor
import net.insprill.robotinsprill.audit.AuditManager

class AuditMembers(robot: RobotInsprill, audit: AuditManager) : AuditCategory(robot, audit) {

    override fun registerEvents() {
        val config = robot.config.audit.events.members
        event<BanAddEvent>(config.banned) {
            send(user, AuditColor.RED, "<@${user.id}> was banned.")
        }
        event<BanRemoveEvent>(config.unbanned) {
            send(user, AuditColor.GREEN, "<@${user.id}> was unbanned.")
        }
        event<MemberJoinEvent>(config.joined) {
            send(member, AuditColor.GREEN, "<@${member.id}> has joined.")
        }
        event<MemberLeaveEvent>(config.left) {
            send(user, AuditColor.GREEN, "<@${user.id}> has left.")
        }
        event<VoiceStateUpdateEvent>(config.voice) {
            if (state.channelId == null) {
                send(
                    state.getMember(),
                    AuditColor.RED,
                    "<@${state.getMember().id}> has left <#${old?.channelId}>."
                )
            } else {
                send(
                    state.getMember(),
                    AuditColor.GREEN,
                    "<@${state.getMember().id}> has joined <#${state.channelId}>."
                )
            }
        }
    }

}
