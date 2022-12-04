package me.Insprill.RI.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerSettings {

    public static HashMap<String, String> serverPrefix;
    public static HashMap<String, List<String>> serverModRoles;
    public static HashMap<String, List<String>> suggestionChannels;
    public static HashMap<String, Integer> suggestionMinChars;
    public static HashMap<String, String> antiSpam;
    public static HashMap<String, String> auditLog;

    // Roles
    public static boolean isMod(Guild guild, Member member) {
        if (member.hasPermission(Permission.ADMINISTRATOR))
            return true;
        List<String> modRoles = ServerSettings.getModRoles(guild);
        if (modRoles == null)
            return false;
        for (Role role : member.getRoles())
            if (modRoles.contains(role.getId()))
                return true;
        return false;
    }

    public static List<String> getModRoles(Guild guild) {
        return serverModRoles.get(guild.getId());
    }

    public static void addModRole(Guild guild, Role role) {
        List<String> modRoles = serverModRoles.get(guild.getId());
        if (modRoles == null) modRoles = new ArrayList<>();
        modRoles.add(role.getId());
        serverModRoles.put(guild.getId(), modRoles);
    }

    public static void removeModRole(Guild guild, Role role) {
        List<String> modRoles = serverModRoles.get(guild.getId());
        modRoles.remove(role.getId());
        serverModRoles.put(guild.getId(), modRoles);
    }

    // Prefix
    public static String getPrefix(Guild guild) {
        if (serverPrefix.get(guild.getId()) == null) return "$";
        return serverPrefix.get(guild.getId());
    }

    public static void setPrefix(MessageChannel channel, Guild guild, String prefix) {
        serverPrefix.put(guild.getId(), prefix);
        channel.sendMessage("Set prefix to \"" + prefix + "\"").queue();
    }

    // Suggestions
    public static void addSuggestionChannel(MessageChannel channel, Guild guild, String channelId) {
        List<String> suggestionChannel = suggestionChannels.get(guild.getId());
        if (suggestionChannel == null)
            suggestionChannel = new ArrayList<>();
        if (suggestionChannel.contains(channelId)) {
            channel.sendMessage("\"" + channelId + "\" is already a suggestions channel!").queue();
            return;
        }
        suggestionChannel.add(channelId);
        suggestionChannels.put(guild.getId(), suggestionChannel);
        channel.sendMessage("Successfully made \"" + channelId + "\" a suggestions channel!").queue();
    }

    public static void removeSuggestionChannel(MessageChannel channel, Guild guild, String channelId) {
        List<String> suggestionChannel = suggestionChannels.get(guild.getId());
        if (suggestionChannel == null || suggestionChannel.isEmpty()) {
            channel.sendMessage("You don't have any suggestions channels!").queue();
            return;
        }
        if (!suggestionChannel.contains(channelId)) {
            channel.sendMessage("That isn't a suggestion channel!").queue();
            return;
        }
        suggestionChannel.remove(channelId);
        suggestionChannels.put(guild.getId(), suggestionChannel);
        channel.sendMessage("Successfully removed \"" + channelId + "\" as a suggestions channel!").queue();
    }

    public static boolean isSuggestionChannel(Guild guild, String channelId) {
        List<String> suggestionChannel = suggestionChannels.get(guild.getId());
        return suggestionChannel != null && !suggestionChannel.isEmpty() && suggestionChannel.contains(channelId);
    }

    public static List<String> getSuggestionsChannels(Guild guild) {
        return suggestionChannels.get(guild.getId());
    }

    public static void setSuggestionMinChars(MessageChannel channel, Guild guild, int min) {
        suggestionMinChars.put(guild.getId(), min);
        channel.sendMessage("Successfully changed minimum amount of suggestion character to " + min + "!").queue();
    }

    public static int getSuggestionMinChars(Guild guild) {
        return suggestionMinChars.getOrDefault(guild.getId(), 0);
    }

    public static void setAntiSpamEnabled(Guild guild, boolean enable) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        jo.addProperty("enabled", enable);
        antiSpam.put(guild.getId(), jo.toString());
    }

    public static void setAntiSpamExpire(Guild guild, long expire) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        jo.addProperty("expire", expire);
        antiSpam.put(guild.getId(), jo.toString());
    }

    public static void setAntiSpamMaxMessages(Guild guild, long expire) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        jo.addProperty("max", expire);
        antiSpam.put(guild.getId(), jo.toString());
    }

    public static void setAntiSpamDifference(Guild guild, long expire) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        jo.addProperty("diff", expire);
        antiSpam.put(guild.getId(), jo.toString());
    }

    public static boolean getAntiSpamEnabled(Guild guild) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        if (jo.get("enabled") == null)
            return false;
        return jo.get("enabled").getAsBoolean();
    }

    public static long getAntiSpamExpire(Guild guild) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        if (jo.get("expire") == null)
            return 2;
        return jo.get("expire").getAsLong();
    }

    public static long getAntiSpamMaxMessages(Guild guild) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        if (jo.get("max") == null)
            return 5;
        return jo.get("max").getAsLong();
    }

    public static long getAntiSpamDifference(Guild guild) {
        JsonObject jo = (antiSpam.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(antiSpam.get(guild.getId()), JsonObject.class);
        if (jo.get("diff") == null)
            return 3;
        return jo.get("diff").getAsLong();
    }

    public static void setAuditLogChannel(Guild guild, String channelId) {
        JsonObject jo = (auditLog.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(auditLog.get(guild.getId()), JsonObject.class);
        jo.addProperty("channel", channelId);
        auditLog.put(guild.getId(), jo.toString());
    }

    public static MessageChannel getAuditLogChannel(Guild guild) {
        JsonObject jo = (auditLog.get(guild.getId()) == null) ? new JsonObject() : new Gson().fromJson(auditLog.get(guild.getId()), JsonObject.class);
        if (jo.get("channel") == null)
            return null;
        String id = jo.get("channel").getAsString();
        return guild.getTextChannelById(id);
    }

}
