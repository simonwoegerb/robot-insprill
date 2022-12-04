package me.Insprill.RI.misc;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Join extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        event.getGuild().getTextChannels().get(0).sendMessage("Thanks for inviting me! For help type \"$help\" or to change the prefix type \"$setprefix <prefix>\"").queue();
    }
}
