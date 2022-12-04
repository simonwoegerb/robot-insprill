package me.Insprill.RI.custom.zonemc;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class AccountMustBeLinked extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.ZoneMCGuildId)) return;
        Message msg = event.getMessage();
        MessageChannel channel = msg.getChannel();
        if (channel.getId().equals(IDs.ZoneMCMemesChannel) || channel.getId().equals(IDs.ZoneMCSuggestionsChannel))
            return;
        if (event.getAuthor().isBot()) return;

        RobotInsprill.executor.execute(() -> {

            Member member = msg.getMember();

            if (member == null) return;

            Role role = event.getGuild().getRoleById(IDs.ZoneMCLinkedRoledId);

            if (!member.getRoles().contains(role)) {

                msg.delete().queue();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(msg.getAuthor().getName() + ", you must have your account linked to send messages in this channel!");
                eb.setDescription("To do this, login to the server (`play.zonemc.net`), and type \"/discord link\" then follow the instructions.");
                eb.setColor(new Color(50, 200, 255));
                channel.sendMessageEmbeds(eb.build()).queue(e -> e.delete().queueAfter(10000, TimeUnit.MILLISECONDS));

            }
        });
    }

}
