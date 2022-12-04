package me.Insprill.RI.featues;

import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AuditLog extends ListenerAdapter {

    public static final Color LIGHT_RED = new Color(240, 72, 72);

    public static void log(Guild guild, Member member, String description, String footer, Color color) {
        MessageChannel channel = ServerSettings.getAuditLogChannel(guild);
        if (channel == null)
            return;
        EmbedBuilder eb = new EmbedBuilder();
        if (member != null)
            eb.setAuthor(member.getUser().getAsTag(), null, member.getUser().getEffectiveAvatarUrl());
        eb.setDescription(description);
        eb.setColor(color);
        eb.setFooter(footer);
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        Guild guild = event.getGuild();
        if (!event.getMessage().getContentRaw().startsWith(ServerSettings.getPrefix(guild) + "auditlog"))
            return;
        MessageChannel channel = event.getChannel();
        if (!ServerSettings.isMod(guild, event.getMember())) {
            channel.sendMessage("You don't have permission to use this command!").queue();
            return;
        }
        String msg = StringUtils.trim(event.getMessage().getContentRaw());
        if (msg.equalsIgnoreCase(ServerSettings.getPrefix(guild) + "auditlog")) {
            channel.sendMessage("Incorrect usage! Type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for help.").queue();
            return;
        }
        String command = StringUtils.replace(msg, ServerSettings.getPrefix(guild) + "auditlog ", "").toLowerCase();
        String[] args = command.split(" ");
        if (args.length == 1) {
            switch (args[0]) {
                case "set":
                    channel.sendMessage("You need to specify a channel id!").queue();
                    break;
                case "remove":
                    ServerSettings.setAuditLogChannel(guild, null);
                    channel.sendMessage("Audit log channel successfully removed!").queue();
                    break;
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "set":
                    if (args[1].matches("^[0-9]+$")) {
                        ServerSettings.setAuditLogChannel(guild, args[1]);
                        channel.sendMessage("Successfully set audit log channel to <#" + args[1] + ">!").queue();
                    }
                    break;
            }
        }
    }

}
