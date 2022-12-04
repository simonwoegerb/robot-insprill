package me.Insprill.RI.featues;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ScammerBanner extends ListenerAdapter {

    private static final String[] e = { "https", "http" };

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        Guild guild = event.getGuild();
        if (!guild.getId().equals(IDs.CodedRedGuildId) && !guild.getId().equals(IDs.InsprillDevelopmentGuildId) && !guild.getId().equals(IDs.BotTestingGuildId) && !guild.getId().equals(IDs.ZoneMCGuildId))
            return;

        RobotInsprill.executor.execute(() -> {
            Message msg = event.getMessage();
            String content = msg.getContentRaw();

            try {
                Member member = guild.retrieveMemberById(msg.getAuthor().getId()).complete();
                if (ServerSettings.isMod(guild, member))
                    return;
            } catch (Exception ignored) {
            }

            if (!StringUtils.containsIgnoreCase(content, "@everyone") && !StringUtils.containsIgnoreCase(content, "@here"))
                return;
            if (!StringUtils.containsAnyIgnoreCase(content, e))
                return;

            msg.delete().queue();
            msg.getMember().ban(7, "Posting scam link.").queue();

            AuditLog.log(msg.getGuild(), msg.getMember(), "**Banned <@" + msg.getMember().getId() + "> for posting scam link in <#" + msg.getChannel().getId() + ">.**\n" +
                            "```\n" + content + "```",
                    "Message ID: " + msg.getId() + " \u2022 Today at " + RobotInsprill.getCurrentDateFormatted()
                    , AuditLog.LIGHT_RED);
        });
    }

}
