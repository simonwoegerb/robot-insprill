package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AdvertisingCooldown extends ListenerAdapter {

    public static HashMap<String, Long> cooldown = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.CodedRedGuildId)) return;
        if (!event.getChannel().equals(event.getGuild().getTextChannelById(IDs.CodedRedAdvertiseMcServers))) return;
        Message msg = event.getMessage();
        if (event.getAuthor().isBot()) return;
        if (msg.getAuthor().getId().equals(IDs.InsprillId) || msg.getAuthor().getId().equals(IDs.CodedRedId))
            return;
        RobotInsprill.executor.execute(() -> {
            MessageChannel channel = event.getChannel();

            try (
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/codedred/cooldown.ser"))) {

                long currentTime = System.currentTimeMillis();
                long oneWeek = 604800000L; // 604800000 = 1 week

                if (cooldown.containsKey(msg.getAuthor().getId())) {

                    long time = cooldown.get(msg.getAuthor().getId());

                    if (time + oneWeek > System.currentTimeMillis()) {
                        msg.delete().queue();

                        DateFormat simple = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        Date result = new Date(time + oneWeek);

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("You can only advertise once a week!");
                        eb.setDescription("The next time you can advertise is " + simple.format(result));
                        eb.setColor(new Color(255, 0, 0));
                        channel.sendMessageEmbeds(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

                        Logger.getInstance().addToLog("tried to advertise more then 1 time per week (The next time they can advertise is " + simple.format(result) + ")", msg.getAuthor().getName());

                    }
                    else {

                        cooldown.put(msg.getAuthor().getId(), currentTime);
                        Logger.getInstance().addToLog("advertised their server", msg.getAuthor().getName());
                        oos.writeObject(cooldown);

                    }

                }
                else {
                    // Advertisements under 200 chars
                    if (msg.getContentRaw().length() < 512) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Advertisements must be over 512 characters!");
                        eb.setColor(new Color(255, 0, 0));
                        eb.setDescription("Your advertisement length: " + msg.getContentRaw().length());
                        channel.sendMessageEmbeds(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                        return;
                    }

                    // Delete if under 7 lines.
                    int lineCount = StringUtils.countMatches(event.getMessage().getContentRaw(), "\n") + 1;
                    if (lineCount < 7) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Advertisements must have 7 lines!");
                        eb.setColor(new Color(255, 0, 0));
                        eb.setDescription("Your advertisements line count: " + lineCount);
                        channel.sendMessageEmbeds(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                        event.getMessage().delete();
                    }

                    else {
                        cooldown.put(msg.getAuthor().getId(), currentTime);
                        Logger.getInstance().addToLog("advertised their server", msg.getAuthor().getName());
                        oos.writeObject(cooldown);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });
    }
}
