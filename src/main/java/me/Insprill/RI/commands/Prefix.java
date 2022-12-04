package me.Insprill.RI.commands;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Prefix extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        RobotInsprill.executor.execute(() -> {
            Message rmsg = event.getMessage();
            if (!rmsg.getContentRaw().startsWith(ServerSettings.getPrefix(event.getGuild()) + "setprefix")) return;
            if (!ServerSettings.isMod(event.getGuild(), event.getMember())) {
                event.getChannel().sendMessage("You don't have permissions to do that!").queue(e -> e.delete().queueAfter(10000, TimeUnit.MILLISECONDS));
                return;
            }
            String prefix = StringUtils.replace(rmsg.getContentRaw(), ServerSettings.getPrefix(event.getGuild()) + "setprefix ", "");
            ServerSettings.setPrefix(rmsg.getChannel(), rmsg.getGuild(), prefix);
        });
    }
}
