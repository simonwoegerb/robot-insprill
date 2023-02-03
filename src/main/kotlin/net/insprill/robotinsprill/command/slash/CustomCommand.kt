package net.insprill.robotinsprill.command.slash

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import net.insprill.robotinsprill.configuration.BotConfig

class CustomCommand(
    override val name: String,
    override val description: String,
    private val cmd: BotConfig.Commands.Slash.CustomCommand
) :
    SlashCommand() {

    override suspend fun execute(context: ChatInputCommandInteractionCreateEvent) {
        val res: InteractionResponseCreateBuilder.() -> Unit = {
            content = cmd.response.text
            cmd.response.embeds()?.let { embeds.addAll(it) }
        }
        if (cmd.private) {
            context.interaction.respondEphemeral(res)
        } else {
            context.interaction.respondPublic(res)
        }
    }

    companion object {
        fun buildCommandArray(config: BotConfig): Array<SlashCommand> {
            return config.commands.slash.custom.map {
                CustomCommand(it.name, it.description, it)
            }.toTypedArray()
        }
    }

}
