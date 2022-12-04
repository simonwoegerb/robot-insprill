package me.Insprill.RI.misc;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Ai extends ListenerAdapter {

    final List<String> pingResponses = new PingResponses();
    final List<String> pingReactionResponses = new PingReactionResponses();
    final List<String> pingReactionReactionResponses = new PingReactionReactionResponses();
    final List<String> pingReactionReactionReactionResponses = new PingReactionReactionReactionResponses();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().contains(event.getGuild().getMemberById("709765584342089781")))
            event.getChannel().sendMessage(pingResponses.get(new SecureRandom().nextInt(pingResponses.size()))).queue();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (event.getUser().isBot()) return;
        event.getChannel().getHistory().retrievePast(3).queue(messages -> {
            for (Message message : messages) {
                if (message.getAuthor().getId().equals(IDs.RobotInsprillId)) {
                    if (!message.getId().equals(event.getMessageId())) continue;

                    // Responses to reacting to ping messages.
                    if (pingResponses.contains(message.getContentRaw())) {
                        event.getChannel().sendMessage(pingReactionResponses.get(new SecureRandom().nextInt(pingReactionResponses.size()))).queue();
                        return;
                    }

                    // Responses to reacting to ping messages.
                    if (pingReactionResponses.contains(message.getContentRaw())) {
                        event.getChannel().sendMessage(pingReactionReactionResponses.get(new SecureRandom().nextInt(pingReactionReactionResponses.size()))).queue();
                        return;
                    }

                    // Responses to reacting to ping messages.
                    if (pingReactionReactionResponses.contains(message.getContentRaw())) {
                        event.getChannel().sendMessage(pingReactionReactionReactionResponses.get(new SecureRandom().nextInt(pingReactionReactionReactionResponses.size()))).queue();
                        return;
                    }

                }
            }
        });
    }

    private static class PingResponses extends ArrayList<String> {
        {
            add("Why are you pinging me huh?");
            add("What are you expecting, to get a response?");
            add("don't. ping. me.");
            add("imagine pinging a bot.");
            add("did you actually just ping a bot....");
            add("don't ping me");
        }
    }

    private static class PingReactionResponses extends ArrayList<String> {
        {
            add("and now you react to it?");
            add("testing your luck eh...");
            add("better stop mate");
            add("stop.");
            add("mate can you not?");
        }
    }

    private static class PingReactionReactionResponses extends ArrayList<String> {
        {
            add("mate you are really testing your luck.");
            add("dawg you better stop");
            add("hahaha you think it's funny? If you keep this up you won't be laughing any more.");
            add("i will find where you live.");
        }
    }

    private static class PingReactionReactionReactionResponses extends ArrayList<String> {
        {
            add("You're still reacting to my messages you little bitch? I'll have you know I graduated top of my class in the Navy Seals, and I've been involved in numerous secret raids on Al-Qaeda, and I have over 300 confirmed kills. I am trained in gorilla warfare and I'm the top sniper in the entire US armed forces. You are nothing to me but just another target. I will wipe you the fuck out with precision the likes of which has never been seen before on this Earth, mark my fucking words. You think you can get away with saying that shit to me over the Internet? Think again, fucker. As we speak I am contacting my secret network of spies across the USA and your IP is being traced right now so you better prepare for the storm, maggot. The storm that wipes out the pathetic little thing you call your life. You're fucking dead, kid. I can be anywhere, anytime, and I can kill you in over seven hundred ways, and that's just with my bare hands. Not only am I extensively trained in unarmed combat, but I have access to the entire arsenal of the United States Marine Corps and I will use it to its full extent to wipe your miserable ass off the face of the continent, you little shit. If only you could have known what unholy retribution your little \"clever\" comment was about to bring down upon you, maybe you would have held your fucking tongue. But you couldn't, you didn't, and now you're paying the price, you goddamn idiot. I will shit fury all over you and you will drown in it. You're fucking dead, kiddo.");
            add("You abhorrent scum. You ignorant fool. I will sue you. You are going to be the target of the greatest lawsuit the world has ever seen. You don't know why? It's completely obvious. It's so obvious, the most deplorable peasant could grasp the full magnitude of your wickedness and treachery with the greatest of ease. Your actions cry out for mercy, and I will be happy to deliver it. And if you're thinking this is a mistake, or merely a deception of mine, you're sadly mistaken, my friend. I have indisputable proof of your continued harassment and other offenses. Even without it, the jury would take one look at you and decide. The incomprehensible magnitude of your crimes brings with it unavoidable, infinite guilt, and whether you notice it or not, everyone else does. Are you interested in who will be serving as the offense attorney? I'll tell you. It's my father. Your defense? It doesn't matter, in fact, they might just not give you one even to spare just one individual from the trauma. My father is the greatest lawyer in the US, the world, and human history, including the future, which he knows due to the fact that he sued the future and they travelled back in time to tell him. He's served for hundreds of Supreme Court cases, and he's won every single one. You may have never thought about being court-marshaled, but now, that's a real threat. That is the power of my father, a culmination of flawless, supreme logic and a perfect knowledge of the law. You will lose this case, your money, and your life. Does that scare you, insignificant bug? Because it should. The entire history of the U.S. Judicial System has been leading up to this moment, where all of its fury is concentrated on ruining your life. My father won't even need to help. Your heinous crimes will be evident to all, so just give up, you crook. Give up before you're forced to.");
        }
    }

}
