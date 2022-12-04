package me.Insprill.RI.commands;

import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Help extends ListenerAdapter {


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().equalsIgnoreCase(ServerSettings.getPrefix(event.getGuild()) + "help")) {
            String prefix = ServerSettings.getPrefix(event.getGuild());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("All commands");
            eb.setDescription("`" + prefix + "binfile <message id>`   -   Turns all files in message into a hastebin." + "\n" +
                    "\nFor help with admin commands, please type \"" + ServerSettings.getPrefix(event.getGuild()) + "help-admin\"" +
                    "");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } else if (event.getMessage().getContentRaw().equalsIgnoreCase(ServerSettings.getPrefix(event.getGuild()) + "help-admin")) {

            if (!ServerSettings.isMod(event.getGuild(), event.getMember())) {
                event.getChannel().sendMessage("You do not have permission to use this command!").queue();
                return;
            }
            String prefix = ServerSettings.getPrefix(event.getGuild());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("All admin commands");
            eb.setDescription("`" + prefix + "setprefix <new prefix>`   -   Sets prefix." + "\n" +
                    "`" + prefix + "modrole add <role by ID>`   -   Adds a mod role." + "\n" +
                    "`" + prefix + "modrole remove <role by ID>`   -   Removes a mod role." + "\n" +
                    "`" + prefix + "modrole list`   -   Lists all current mod roles (name & id)." + "\n" +
                    "`" + prefix + "suggestions add/remove <channel ID>`   -   Adds or removes a channel as a suggestions channel." + "\n" +
                    "`" + prefix + "suggestions setminchars <min chars>`   -   Sets the minimum amount of character a suggestion can have." + "\n" +
                    "`" + prefix + "antispam <enable/disable>`   -   Enabled or disables the AntiSpam feature." + "\n" +
                    "`" + prefix + "antispam expire <minutes>`   -   Sets time for a message to expire." + "\n" +
                    "`" + prefix + "antispam diff <number>`   -   Percent difference to consider a message the same." + "\n" +
                    "`" + prefix + "antispam max <number>`   -   Sets max amount of almost identical messages sent in less then expire time until they start getting deleted." + "\n" +
                    "`" + prefix + "auditlog set <channel ID>`   -   Sets the channel Robot Insprill will log actions to." + "\n" +
                    "`" + prefix + "auditlog remove`   -   Stops Robot Insprill from logging actions." + "\n" +
                    "\nFor help with regular commands, please type \"" + ServerSettings.getPrefix(event.getGuild()) + "help\"" +
                    "");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        }
    }
}
