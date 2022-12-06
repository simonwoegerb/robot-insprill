package net.insprill.robotinsprill.command.message

import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.MessageCommandCreateBuilder

abstract class MessageCommand {

    abstract val name: String

    open fun setup(builder: MessageCommandCreateBuilder) {}

    abstract suspend fun execute(context: MessageCommandInteractionCreateEvent)

}
