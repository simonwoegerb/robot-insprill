package net.insprill.robotinsprill.autoaction

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import net.insprill.robotinsprill.configuration.BotConfig
import net.insprill.robotinsprill.extension.message

@Suppress("UNUSED")
enum class Action {
    RESPOND {
        override suspend fun process(message: Message, action: BotConfig.AutoAction.Action) {
            action.responses?.forEach {
                message.reply {
                    message(it)
                }
            }
        }
    },
    REACTION {
        override suspend fun process(message: Message, action: BotConfig.AutoAction.Action) {
            action.reactions?.forEach {
                message.addReaction(it.asReactionEmoji())
            }
        }
    },
    ;

    abstract suspend fun process(message: Message, action: BotConfig.AutoAction.Action)

}
