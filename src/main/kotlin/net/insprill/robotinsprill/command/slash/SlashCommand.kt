package net.insprill.robotinsprill.command.slash

import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import net.insprill.robotinsprill.command.Command

abstract class SlashCommand : Command() {

    abstract val description: String

    open fun setup(builder: ChatInputCreateBuilder) {}

    abstract suspend fun execute(context: ChatInputCommandInteractionCreateEvent)

}
