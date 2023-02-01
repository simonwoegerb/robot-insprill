package net.insprill.robotinsprill.command.message

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.rest.builder.message.create.embed
import java.net.URLEncoder

class Google : MessageCommand() {

    override val name: String
        get() = "Google That"

    override suspend fun execute(context: MessageCommandInteractionCreateEvent) {
        val content = context.interaction.getTarget().content
        val encodedContent = URLEncoder.encode(content, Charsets.UTF_8)
        context.interaction.respondPublic {
            embed {
                title = content
                url = "https://letmegooglethat.com/?q=$encodedContent"
            }
        }
    }

}
