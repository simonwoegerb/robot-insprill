package net.insprill.robotinsprill.extension

import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import net.insprill.robotinsprill.configuration.BotConfig

fun InteractionResponseCreateBuilder.message(msg: BotConfig.Message) = apply {
    this.content = msg.text
    msg.embeds()?.let { this.embeds.addAll(it) }
}
