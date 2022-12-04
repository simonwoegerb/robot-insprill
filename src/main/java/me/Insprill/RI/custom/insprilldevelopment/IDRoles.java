package me.Insprill.RI.custom.insprilldevelopment;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class IDRoles extends ListenerAdapter {

    /*
    Roles IDs:
        Custom-Join-Messages: 689581956467654826
        Custom-Join-Messages-SNAPSHOT: 701215200522797116
     */

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.InsprillDevelopmentGuildId) || event.getAuthor().isBot())
            return;
        if (event.getMessage().getAuthor().isBot()) return;
        RobotInsprill.executor.execute(() -> {
            Message msg = event.getMessage();

            // Make sure its in bot commands channel
            if (msg.getChannel().getId().equals("672122627971743797")) {
                Member member = msg.getMember();
                Guild g = msg.getGuild();

                // CJM
                if (msg.getContentRaw().equals("!cjm")) {

                    Role cjmRole = g.getRoleById("689581956467654826");

                    if (member.getRoles().contains(cjmRole) && member.getRoles() != null) {
                        g.removeRoleFromMember(member, cjmRole).queue();
                        msg.getChannel().sendMessage("The CJM role has been taken away!").queue();
                    } else {
                        g.addRoleToMember(member, cjmRole).queue();
                        msg.getChannel().sendMessage("You have been given the CJM role!").queue();
                    }

                }
            }

        });
    }
}
