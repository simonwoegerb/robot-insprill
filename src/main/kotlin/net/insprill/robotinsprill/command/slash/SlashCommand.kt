package net.insprill.robotinsprill.command.slash

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder

abstract class SlashCommand {

    abstract val name: String
    abstract val description: String

    open fun setup(builder: ChatInputCreateBuilder) {}

    abstract suspend fun execute(context: ChatInputCommandInteractionCreateEvent)

}
