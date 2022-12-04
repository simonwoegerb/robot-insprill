package me.Insprill.RI.commands;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ModRoles extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        RobotInsprill.executor.execute(() -> {
            String msg = StringUtils.trim(event.getMessage().getContentRaw());
            Guild guild = event.getGuild();
            if (!msg.startsWith(ServerSettings.getPrefix(guild) + "modrole")) return;
            MessageChannel channel = event.getChannel();
            if (!ServerSettings.isMod(guild, event.getMember())) {
                channel.sendMessage("You don't have permission to use this command!").queue();
                return;
            }
            if (msg.equalsIgnoreCase(ServerSettings.getPrefix(guild) + "modrole")) {
                channel.sendMessage("Incorrect usage! Type \"" + ServerSettings.getPrefix(guild) + "help-admin\" for help.").queue();
                return;
            }
            String command = StringUtils.replace(msg, ServerSettings.getPrefix(guild) + "modrole ", "").toLowerCase();
            String[] args = command.split(" ");
            if (args[0].equals("add") || args[0].equals("remove")) {
                if (args.length <= 1) {
                    channel.sendMessage("You need to specify a role ID!").queue();
                }
                else {
                    if (args[1].matches("^[0-9]+$")) {
                        Role role = guild.getRoleById(args[1]);
                        if (role == null) {
                            channel.sendMessage("Could not find role by id `" + args[1] + "`!").queue();
                            return;
                        }
                        if (args[0].equals("add")) {
                            ServerSettings.addModRole(guild, role);
                            channel.sendMessage("Added " + role.getName() + " as a moderator role!").queue();
                        }
                        else if (args[0].equals("remove")) {
                            if (ServerSettings.getModRoles(guild).contains(args[1]) || ServerSettings.getModRoles(guild).isEmpty() || ServerSettings.getModRoles(guild) == null) {
                                ServerSettings.removeModRole(guild, role);
                                channel.sendMessage("Removed " + role.getName() + " as a moderator role!").queue();
                            }
                            else {
                                channel.sendMessage("That's not a mod role!").queue();
                            }
                        }
                    }
                }
            }
            else if (args[0].equals("list")) {
                if (ServerSettings.getModRoles(guild) == null || ServerSettings.getModRoles(guild).isEmpty()) {
                    channel.sendMessage("There aren't any mod roles!").queue();
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("All current mod roles are:\n");
                for (String id : ServerSettings.getModRoles(guild)) {
                    Role role = guild.getRoleById(id);
                    builder.append(role.getName()).append(" (").append(id).append(")\n");
                }
                channel.sendMessage(builder).queue();
            }
        });
    }

}
