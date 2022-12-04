package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.utils.GitHubUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Patron extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        MessageChannel channel = event.getChannel();
//        if (!channel.getId().equals(IDs.CodedRedRequestGitHubAccess))
//            return;
        if (!channel.getId().equals("709992661389410395"))
            return;
        Message message = event.getMessage();
        if (message.getAuthor().isBot())
            return;
        String messageContent = message.getContentRaw();
        String githubUserName;
        byte patronTier = 0;
        if (messageContent.startsWith("https://github.com/")) {
            githubUserName = StringUtils.replace(messageContent, "https://github.com/", "");
        } else {
            githubUserName = message.getContentRaw();
        }

        if (!GitHubUtils.userExists(githubUserName)) {
            channel.sendMessage("I couldn't find a user by the name of " + githubUserName + "!").queue();
            return;
        }

        List<Role> roles = event.getMember().getRoles();
        Role patronTier1 = event.getGuild().getRoleById(IDs.CodedRedPatronTier1);
        Role patronTier2 = event.getGuild().getRoleById(IDs.CodedRedPatronTier2);
        Role patronTier3 = event.getGuild().getRoleById(IDs.CodedRedPatronTier3);
        Role patronTier4 = event.getGuild().getRoleById(IDs.CodedRedPatronTier4);
        Role patronTier5 = event.getGuild().getRoleById(IDs.CodedRedPatronTier5);
        Role patronTier6 = event.getGuild().getRoleById(IDs.CodedRedPatronTier6);
        if (roles.contains(patronTier1))
            patronTier = 1;
        if (roles.contains(patronTier2))
            patronTier = 2;
        if (roles.contains(patronTier3))
            patronTier = 3;
        if (roles.contains(patronTier4))
            patronTier = 4;
        if (roles.contains(patronTier5))
            patronTier = 5;
        if (roles.contains(patronTier6))
            patronTier = 6;

        switch (patronTier) {
            case 6:
            case 5:
            case 4:
            case 3:
                respond(channel, "Additional_Helpful_Code", GitHubUtils.addMemberToOrganizationRepo("CodedRedYT", "Additional_Helpful_Code", githubUserName));
            case 2:
                respond(channel, "Exclusive-Video-Series", GitHubUtils.addMemberToOrganizationRepo("CodedRedYT", "Exclusive-Video-Series", githubUserName));
            case 1:
                respond(channel, "Beginner_1.15_Series", GitHubUtils.addMemberToOrganizationRepo("CodedRedYT", "Beginner_1.15_Series", githubUserName));
                respond(channel, "Advanced_Spigot_Series", GitHubUtils.addMemberToOrganizationRepo("CodedRedYT", "Advanced_Spigot_Series", githubUserName));
        }
        channel.sendMessage("To accept the invite to CodedRed's organization go here: https://github.com/CodedRedYT\n" +
                "Once there you will be able to view the repositories that your team has access to!\n" +
                "Thank you again for supporting! :mcheart:").queue();
        message.delete().queue();

    }

    private void respond(MessageChannel channel, String repoName, int responseCode) {
        switch (responseCode) {
            case GitHubUtils.RATE_LIMITED -> channel.sendMessage("Oh no! I've been rate limited! Please try again at " + GitHubUtils.getRateLimitExpire()).queue();
            case GitHubUtils.ERROR -> channel.sendMessage("Oh no! An error occurred while adding you to the repo! Please try again later.").queue();
            case GitHubUtils.SUCCESS -> channel.sendMessage("You have been added to the \"" + repoName + "\" repo!").queue();
            case GitHubUtils.ALREADY_ADDED -> channel.sendMessage("You're already added to the \"" + repoName + "\" repo!").queue();
        }
    }

}
