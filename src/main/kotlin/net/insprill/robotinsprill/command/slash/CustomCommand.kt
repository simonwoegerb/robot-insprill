package net.insprill.robotinsprill.command.slash

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.embed
import net.insprill.robotinsprill.configuration.BotConfig

class CustomCommand(
    override val name: String,
    override val description: String,
    private val response: BotConfig.Commands.Slash.CustomCommand
) :
    SlashCommand() {

    override suspend fun execute(context: ChatInputCommandInteractionCreateEvent) {
        val res = if (!response.message.isNullOrBlank()) {
            createMessage()
        } else {
            createEmbed()
        }
        if (response.private) {
            context.interaction.respondEphemeral(res)
        } else {
            context.interaction.respondPublic(res)
        }
    }

    private fun createMessage(): InteractionResponseCreateBuilder.() -> Unit {
        return { content = response.message }
    }

    private fun createEmbed(): InteractionResponseCreateBuilder.() -> Unit {
        return {
            embed {
                title = response.embed?.title
                description = response.embed?.description
                url = response.embed?.url
                timestamp = response.embed?.timestamp
                color = response.embed?.color
                image = response.embed?.image
                footer = response.embed?.footer?.kord()
                thumbnail = response.embed?.thumbnail?.let { EmbedBuilder.Thumbnail().apply { url = it } }
                author = response.embed?.author?.kord()
//                fields = response.embed?.fields ?: ArrayList()
            }
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
