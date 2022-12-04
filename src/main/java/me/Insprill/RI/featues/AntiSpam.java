package me.Insprill.RI.featues;

import me.Insprill.RI.misc.ThreadHandler;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AntiSpam extends ListenerAdapter {

    public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(ThreadHandler.createThreadFactory("AntiSpam Processor", 2));
    private final Map<String, Map<String, String>> map = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        executor.execute(() -> {
            String message = event.getMessage().getContentRaw();
            message = message.toLowerCase();
            Guild guild = event.getGuild();
            if (message.startsWith(ServerSettings.getPrefix(guild) + "antispam")) {
                commandHandler(event);
                return;
            }
            if (!ServerSettings.getAntiSpamEnabled(guild))
                return;
            if (ServerSettings.isMod(guild, event.getMember()))
                return;
            message = StringUtils.stripAccents(message);
            message = StringUtils.deleteWhitespace(message);
            Map<String, String> tempMap = map.getOrDefault(guild.getId(), new HashMap<>());
            tempMap.put(event.getMessage().getId(), message);
            map.put(guild.getId(), tempMap);

            executor.schedule(() -> {
                Map<String, String> tempMap2 = map.get(guild.getId());
                if (tempMap2 == null || tempMap2.isEmpty()) return;
                tempMap2.remove(event.getMessage().getId());
                map.put(guild.getId(), tempMap2);
            }, ServerSettings.getAntiSpamExpire(guild), TimeUnit.MINUTES);

            int i = 0;
            double diff = 100.0D;
            for (String str : tempMap.values()) {
                double levDistance = LevenshteinDistance.getDefaultInstance().apply(str, message);
                diff = (levDistance / str.length()) * 100D;
                if (diff > ServerSettings.getAntiSpamDifference(guild))
                    continue;
                i++;
            }
            if (i <= ServerSettings.getAntiSpamMaxMessages(guild))
                return;
            event.getMessage().delete().queue();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            String dateString = dateFormat.format(new Date());
            AuditLog.log(guild, event.getMember(),
                    ":wastebasket: **Deleted spam message sent by <@" + event.getAuthor().getId() + "> in <#" + event.getChannel().getId() + ">**\n" +
                            message + "\n\n" +
                            "*(" + diff + "% difference, " + i + "th message)*",
                    "Message ID: " + event.getMessage().getId() + " \u2022 Today at " + dateString,
                    AuditLog.LIGHT_RED);
        });
    }

    private void commandHandler(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if (!ServerSettings.isMod(guild, event.getMember())) {
            channel.sendMessage("You don't have permission to use this command!").queue();
            return;
        }
        String msg = StringUtils.trim(event.getMessage().getContentRaw());
        if (msg.equalsIgnoreCase(ServerSettings.getPrefix(guild) + "antispam")) {
            channel.sendMessage("Incorrect usage! Type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for help.").queue();
            return;
        }
        String command = StringUtils.replace(msg, ServerSettings.getPrefix(guild) + "antispam ", "").toLowerCase();
        String[] args = command.split(" ");
        if (args.length == 1) {
            switch (args[0]) {
                case "enable":
                    if (ServerSettings.getAntiSpamEnabled(event.getGuild()))
                        channel.sendMessage("AntiSpam is already enabled! type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for configuration options.").queue();
                    else {
                        ServerSettings.setAntiSpamEnabled(guild, true);
                        channel.sendMessage("AntiSpam has been enabled! type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for configuration options.").queue();
                    }
                    break;
                case "disable":
                    if (!ServerSettings.getAntiSpamEnabled(event.getGuild()))
                        channel.sendMessage("AntiSpam is already disabled!").queue();
                    else {
                        ServerSettings.setAntiSpamEnabled(guild, false);
                        channel.sendMessage("AntiSpam has been disabled!").queue();
                    }
                    break;
                case "expire":
                    channel.sendMessage("You need to specify a time in minutes that each message will expire after!").queue();
                    break;
                case "max":
                    channel.sendMessage("You need to specify a max amount of almost identical messages sent in less then expire time until they start getting deleted.").queue();
                    break;
                case "diff":
                    channel.sendMessage("You need to specify a number!").queue();
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "expire":
                    if (args[1].matches("^[0-9]+$")) {
                        long l = Long.parseLong(args[1]);
                        ServerSettings.setAntiSpamExpire(guild, l);
                        channel.sendMessage("Set spam message expire time to `" + l + "`!").queue();
                    } else {
                        channel.sendMessage("\"" + args[1] + "\" is not a number!").queue();
                    }
                    break;
                case "max":
                    if (args[1].matches("^[0-9]+$")) {
                        long l = Long.parseLong(args[1]);
                        ServerSettings.setAntiSpamMaxMessages(guild, l);
                        channel.sendMessage("Set max spam messages to `" + l + "`!").queue();
                    } else {
                        channel.sendMessage("\"" + args[1] + "\" is not a number!").queue();
                    }
                    break;
                case "diff":
                    if (args[1].matches("^[0-9]+$")) {
                        long l = Long.parseLong(args[1]);
                        long diff = (l > 100) ? 100 : l;
                        ServerSettings.setAntiSpamDifference(guild, diff);
                        channel.sendMessage("Set max difference to `" + diff + "`!").queue();
                    } else {
                        channel.sendMessage("\"" + args[1] + "\" is not a number!").queue();
                    }
                    break;
            }
        }
    }


}
