package me.Insprill.RI.commands;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.custom.codedred.ParseError;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.storage.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BinFile extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        // Auto bin logs for CodedReds server.
        Message message = event.getMessage();
        if (event.getGuild().getId().equals(IDs.CodedRedGuildId)) {
            if (!message.getAttachments().isEmpty())
                for (Message.Attachment attachment : message.getAttachments()) {
                    if ("log".equalsIgnoreCase(attachment.getFileExtension())) {
                        bin(message, event.getChannel());
                    }
                }
        }
        if (!message.getContentRaw().startsWith(ServerSettings.getPrefix(event.getGuild()) + "binfile"))
            return;
        MessageChannel channel = event.getChannel();
        channel.sendTyping().queue();
        RobotInsprill.executor.execute(() -> {
            if (message.getReferencedMessage() != null) {
                bin(message.getReferencedMessage(), channel);
                return;
            }
            String id = StringUtils.replace(message.getContentRaw(), ServerSettings.getPrefix(event.getGuild()) + "binfile ", "");
            if (id.isEmpty()) {
                channel.sendMessage("You need to specify a message ID or reply to a message with an attached file! You can see how to get a message ID here: https://support.discord.com/hc/en-us/articles/206346498").queue(e -> e.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            if (!RobotInsprill.isInteger(id)) {
                channel.sendMessage("That's not a valid ID! You can see how to get a message ID here: https://support.discord.com/hc/en-us/articles/206346498").queue(e -> e.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            channel.retrieveMessageById(id).queue(retrievedMessage -> {
                if (retrievedMessage == null) {
                    channel.sendMessage("That message could not be found!").queue(e -> e.delete().queueAfter(10, TimeUnit.SECONDS));
                    return;
                }
                if (retrievedMessage.getAttachments().isEmpty()) {
                    channel.sendMessage("That message doesn't have any files attached to it!").queue(e -> e.delete().queueAfter(10, TimeUnit.SECONDS));
                    return;
                }
                bin(retrievedMessage, channel);
            });
        });
    }

    public String createBin(List<String> lines, String extension) {
        StringBuilder builder = new StringBuilder();
        for (String line : lines)
            builder.append(line)
                    .append("\n");

        try {
            URL url = new URL("https://paste.insprill.net/documents");
            HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            http.setDoOutput(true);
            http.setConnectTimeout(10 * 1000);
            http.setReadTimeout(10 * 1000);
            http.getOutputStream().write(builder.toString().getBytes(Charsets.UTF_8));
            JsonObject object = new Gson().fromJson(new InputStreamReader(http.getInputStream(), Charsets.UTF_8), JsonObject.class);
            return "https://paste.insprill.net/" + object.get("key").getAsString() + "." + extension;
        } catch (Exception exception) {
            return "Could not create link! Error: " + exception.getMessage();
        }

    }

    private void bin(Message message, MessageChannel channel) {
        for (Message.Attachment attachment : message.getAttachments()) {
            String fileExtension = attachment.getFileExtension().toLowerCase();
            if (fileExtension != null) {
                File file = ParseError.downloadFile(attachment);
                if (file == null) {
                    channel.sendMessage("An error occurred while getting the file.").queue();
                    return;
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                    String line;
                    List<String> lines = new ArrayList<>();
                    while ((line = br.readLine()) != null)
                        lines.add(line);
                    file.delete();
                    channel.sendMessage(createBin(lines, fileExtension)).queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
