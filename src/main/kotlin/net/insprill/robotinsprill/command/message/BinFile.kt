package net.insprill.robotinsprill.command.message

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent

class BinFile : MessageCommand() {

    override val name: String
        get() = "binfile"

    override suspend fun execute(context: MessageCommandInteractionCreateEvent) {
        context.interaction.respondPublic {
            content = context.interaction.getTarget().content
        }
    }

}
