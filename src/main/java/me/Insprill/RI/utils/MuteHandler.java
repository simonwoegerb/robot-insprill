package me.Insprill.RI.utils;

import me.Insprill.RI.featues.AuditLog;
import me.Insprill.RI.misc.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class MuteHandler {

    /**
     * Adds time to a users mute duration.
     *
     * @param guild    Guild the user is in.
     * @param userId   The ID of the user to add mute time too.
     * @param delay    The time from now to execution.
     * @param timeUnit The time unit of the delay parameter.
     */
    public static void muteForTime(Guild guild, String userId, long delay, TimeUnit timeUnit) {
        Member member = guild.retrieveMemberById(userId).complete();
        Role muteRole = guild.getRoleById(IDs.CodedRedMutedRoleId);
        if (member == null) {
            AuditLog.log(guild, null, "Tried to mute user with id \"" + userId + "\" but they don't exist!", "", Color.RED);
            return;
        }
        if (muteRole == null) {
            AuditLog.log(guild, member, "Tried to mute <@" + userId + "> but the mute role (ID: " + IDs.CodedRedMutedRoleId + ") couldn't be found!", "", Color.RED);
            return;
        }
        guild.addRoleToMember(member, muteRole).queue();
        guild.removeRoleFromMember(member, muteRole).queueAfter(delay, timeUnit);
    }

}
