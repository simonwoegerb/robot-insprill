package net.insprill.robotinsprill.command.message

import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.MessageCommandCreateBuilder
import net.insprill.robotinsprill.command.Command

abstract class MessageCommand : Command() {

    open fun setup(builder: MessageCommandCreateBuilder) {}

    abstract suspend fun execute(context: MessageCommandInteractionCreateEvent)

}
