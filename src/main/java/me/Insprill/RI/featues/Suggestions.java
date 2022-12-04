package me.Insprill.RI.featues;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Suggestions extends ListenerAdapter {

    public static HashMap<String, List<String>> suggestions = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        Message msg = event.getMessage();
        Guild guild = event.getGuild();
        String prefix = ServerSettings.getPrefix(guild);
        if (msg.getContentRaw().startsWith(prefix + "suggestions")) {
            commandHandler(event);
            return;
        }
        if (!ServerSettings.isSuggestionChannel(event.getGuild(), event.getChannel().getId())) return;
        if (event.getAuthor().isBot()) return;
        if (ServerSettings.isMod(guild, event.getMember()) && msg.getContentRaw().startsWith(prefix + "ds") || msg.getContentRaw().startsWith(prefix + "dontsuggest"))
            return;
        msg.delete().queue();
        RobotInsprill.executor.execute(() -> {
            String suggestion = msg.getContentRaw();
            suggestion = StringUtils.replace(suggestion, "$suggest ", "");
            suggestion = StringUtils.replace(suggestion, "!suggest ", "");
            suggestion = StringUtils.replace(suggestion, "-suggest ", "");
            suggestion = StringUtils.replace(suggestion, prefix + "suggest ", "");

            EmbedBuilder eb = new EmbedBuilder();
            MessageChannel channel = event.getChannel();

            // Suggestion under 32 chars
            if (event.getGuild().getId().equals(IDs.CodedRedGuildId)) {
                int minChars = ServerSettings.getSuggestionMinChars(guild);
                if (suggestion.length() < minChars) {
                    eb.setTitle("Suggestions must be over " + minChars + " characters!");
                    eb.setColor(new Color(255, 0, 0));
                    eb.setDescription("Your suggestion length: " + suggestion.length());
                    channel.sendMessageEmbeds(eb.build()).queue(message ->
                            message.delete().queueAfter(10, TimeUnit.SECONDS));
                    return;
                }
            }

            // Suggestion that meets all requirements
            Member member = event.getMember();

            if (member.getNickname() != null)
                eb.setTitle("Suggestion by: " + member.getNickname() + " (" + msg.getAuthor().getName() + ")");
            else
                eb.setTitle("Suggestion by: " + msg.getAuthor().getName());

            eb.setColor(new Color(255, 0, 0));
            eb.setDescription(suggestion);
            channel.sendMessageEmbeds(eb.build()).queue(message -> {
                List<String> messages = suggestions.get(member.getId()) == null ? new ArrayList<>() : suggestions.get(member.getId());
                messages.add(message.getId());
                suggestions.put(member.getId(), messages);
                message.addReaction("ThumbsUp:789628005106057226").queue();
                message.addReaction("ThumbsDown:789628053118255134").queue();
            });
        });
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (!ServerSettings.isSuggestionChannel(event.getGuild(), event.getChannel().getId())) return;
        if (event.getUser().isBot()) return;
        String userId = event.getUserId();
        if (userId.equals(IDs.InsprillId) || userId.equals(IDs.CodedRedId)) return;
        if (suggestions.get(event.getMember().getId()) != null) {
            if (suggestions.get(event.getMember().getId()).contains(event.getMessageId())) {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
        try {
            if (!event.getReactionEmote().getId().equals("789628005106057226") && !event.getReactionEmote().getId().equals("789628053118255134")) {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
            // We need to catch this since UTF-8 emojis don't have an ID and throw this. thanks Discord -_-
        } catch (IllegalStateException exception) {
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    private void commandHandler(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Message msg = event.getMessage();
        if (!ServerSettings.isMod(guild, event.getMember())) {
            channel.sendMessage("You don't have permission to use this command!").queue();
            return;
        }
        if (msg.getContentRaw().equalsIgnoreCase(ServerSettings.getPrefix(guild) + "suggestions")) {
            channel.sendMessage("Incorrect usage! Type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for help.").queue();
            return;
        }
        String command = StringUtils.replace(msg.getContentRaw(), ServerSettings.getPrefix(guild) + "suggestions ", "").toLowerCase();
        String[] args = command.split(" ");
        switch (args[0]) {
            case "list":
                List<String> suggestionChannels = ServerSettings.getSuggestionsChannels(guild);
                if (suggestionChannels == null || suggestionChannels.isEmpty()) {
                    channel.sendMessage("You don't have any suggestion channels!").queue();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("All current suggestion channels are:").append("\n");
                for (String id : suggestionChannels) {
                    builder.append("<#").append(id).append(">").append("\n");
                }
                channel.sendMessage(builder).queue();
                break;
            case "add":
            case "remove":
                if (args.length == 2) {
                    if (args[1].matches("^[0-9]+$")) {
                        if (args[0].equals("add"))
                            ServerSettings.addSuggestionChannel(event.getChannel(), guild, args[1]);
                        if (args[0].equals("remove"))
                            ServerSettings.removeSuggestionChannel(event.getChannel(), guild, args[1]);
                    } else {
                        channel.sendMessage("\"" + args[1] + "\" is not a valid channel ID!").queue();
                    }
                } else {
                    channel.sendMessage("You need to specify a channel ID!").queue();
                }
                break;
            case "setminchars":
                if (args.length == 1) {
                    channel.sendMessage("You need to specify a minimum amount of characters!").queue();
                } else {
                    if (RobotInsprill.isInteger(args[1])) {
                        ServerSettings.setSuggestionMinChars(event.getChannel(), guild, Integer.parseInt(args[1]));
                    } else {
                        channel.sendMessage("\"" + args[1] + "\" is not a valid number!").queue();
                    }
                }
                break;
        }
    }
}
