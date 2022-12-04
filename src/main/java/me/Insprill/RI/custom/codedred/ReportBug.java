package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportBug extends ListenerAdapter {

    private final HashMap<String, Integer> step = new HashMap<>();
    private String reporter = "";
    private String channelId = "";
    private String plName = "";
    private String MCVersion = "";
    private String plVersion = "";
    private String serverVersion = "";
    private String errors = "";
    private String configs = "";
    private String desc = "";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.CodedRedGuildId)) return;
        if (event.getAuthor().isBot()) return;

        RobotInsprill.executor.execute(() -> {
            Message msg = event.getMessage();
            String senderId = msg.getAuthor().getId();
            if (!step.containsKey(senderId)) {
                step.put(senderId, 0);
            }

            MessageChannel channel = event.getChannel();

            if (msg.getContentRaw().equals("!reportbug") && step.get(senderId) == 0) {
                plName = "";
                MCVersion = "";
                serverVersion = "";
                errors = "";
                configs = "";

                msg.delete().queue();
                reporter = msg.getAuthor().getId();
                channelId = channel.getId();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("What plugin are you reporting a bug for?");
                eb.setColor(new Color(50, 200, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 1);
                return;
            }

            if (!msg.getAuthor().getId().equals(reporter) || !channelId.equals(channel.getId()))
                return;

            // MC Version
            if (step.get(senderId) == 1) {

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                plName = msg.getContentRaw();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("What Minecraft version are you running?");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 2);
                return;
            }

            // Plugin version
            if (step.get(senderId) == 2) {

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                MCVersion = msg.getContentRaw();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("What version of " + plName + " are you running?");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 3);
                return;
            }

            // Server Version
            if (step.get(senderId) == 3) {

                if (msg.getContentRaw().equalsIgnoreCase("latest")) {
                    msg.delete().queue();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Please specify an actual version.");
                    eb.setColor(new Color(55, 205, 255));
                    channel.sendMessageEmbeds(eb.build()).queue(message ->
                            message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                } else {
                    plVersion = msg.getContentRaw();
                }

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("What version of Paper/ Spigot are you running?");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 4);
                return;
            }

            // Errors
            if (step.get(senderId) == 4) {

                if (msg.getContentRaw().equalsIgnoreCase("latest")) {
                    msg.delete().queue();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Please specify an actual version.");
                    eb.setColor(new Color(55, 205, 255));
                    channel.sendMessageEmbeds(eb.build()).queue(message ->
                            message.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                } else {
                    serverVersion = msg.getContentRaw();
                }

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("If there are any errors put them here (PASTEBIN/ HASTEBIN ONLY)");
                eb.setDescription("If there aren't any errors just type \"none\"");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 5);
                return;
            }

            // Config
            if (step.get(senderId) == 5) {

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                errors = msg.getContentRaw();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("If there are any relevant config files put them here (PASTEBIN/ HASTEBIN ONLY)");
                eb.setDescription("If there aren't any just type \"none\"");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 6);
                return;
            }

            // Description
            if (step.get(senderId) == 6) {

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                configs = msg.getContentRaw();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Type out what issue you're having");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 7);
                return;
            }

            // Final (and desc :P)
            if (step.get(senderId) == 7) {

                List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                channel.purgeMessages(messages);

                desc = msg.getContentRaw();
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Bug report by: " + msg.getAuthor().getName());
                eb.appendDescription("Plugin: **" + plName + "**");
                eb.appendDescription(System.lineSeparator() + "Plugin version: **" + plVersion + "**");
                eb.appendDescription(System.lineSeparator() + "MC version: **" + MCVersion + "**");
                eb.appendDescription(System.lineSeparator() + "Server Version: **" + serverVersion + "**");
                if (errors.equalsIgnoreCase("none")) {
                    eb.appendDescription(System.lineSeparator() + "Errors: **N/A**");
                } else {
                    eb.appendDescription(System.lineSeparator() + "Errors: **" + errors + "**");
                }
                if (configs.equalsIgnoreCase("none")) {
                    eb.appendDescription(System.lineSeparator() + "Configs: **N/A**");
                } else {
                    eb.appendDescription(System.lineSeparator() + "Configs: **" + configs + "**");
                }
                eb.appendDescription(System.lineSeparator() + "Issue Description: **" + System.lineSeparator() + desc + "**");
                eb.setColor(new Color(55, 205, 255));
                channel.sendMessageEmbeds(eb.build()).queue();
                step.put(senderId, 0);
            }
        });
    }
}