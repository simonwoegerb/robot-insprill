package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AmogUs extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.CodedRedGuildId)) return;
        if (event.getAuthor().isBot()) return;
        RobotInsprill.lowPriorityExecutor.execute(() -> {
            Emote emote = event.getGuild().retrieveEmoteById(IDs.CodedRedAmongUsEmoji).complete();
            if (!emote.isAvailable())
                return;
            Message message = event.getMessage();
            String text = event.getMessage().getContentRaw();
            text = text.toLowerCase(Locale.ROOT);
            text = StringUtils.stripAccents(text);
            text = StringUtils.replace(text, " ", "");
            text = StringUtils.replace(text, "_", "");
            text = StringUtils.replace(text, "-", "");
            if (text.contains("amongus") || text.contains("amogus")) {
                message.addReaction(emote).queue();
            }
        });
    }

}
