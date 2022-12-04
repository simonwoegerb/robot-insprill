package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.featues.AuditLog;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.utils.MuteHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Showcase extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        int response = handle(event);
        if (response == 0)
            return;
        AuditLog.log(event.getGuild(), event.getMember(), "**Deleted discussion message in <#" + event.getChannel().getId() + "> by <@" + event.getAuthor().getId() + ">.**\n" +
                        "" + event.getMessage().getContentRaw(),
                "Message ID: " + event.getMessage().getId() + " \u2022 Today at " + RobotInsprill.getCurrentDateFormatted()
                , AuditLog.LIGHT_RED);
    }

    private int handle(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT)
            return 0;
        if (!event.getGuild().getId().equals(IDs.CodedRedGuildId))
            return 0;
        if (!event.getMessage().getChannel().getId().equals(IDs.CodedRedShowcase))
            return 0;
        if (!event.getMessage().getAttachments().isEmpty())
            return 0;
        if (event.getMessage().getContentRaw().contains("http"))
            return 0;
        if (event.getAuthor().isBot())
            return 0;
        event.getMessage().delete().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Please don't use the showcase channel for discussions!");
        eb.setDescription("Please use <#" + IDs.CodedRedDiscussion + "> to talk about something that was showcased.");
        eb.setColor(new Color(255, 0, 0));
        event.getChannel().sendMessageEmbeds(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        MuteHandler.muteForTime(event.getGuild(), event.getMember().getId(), 5, TimeUnit.SECONDS);
        return -1;
    }

}
