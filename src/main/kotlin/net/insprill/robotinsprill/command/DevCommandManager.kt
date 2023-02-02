package net.insprill.robotinsprill.command

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.collect
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.command.message.MessageCommand
import net.insprill.robotinsprill.command.slash.SlashCommand

class DevCommandManager(private val robot: RobotInsprill) : CommandManager() {

    override fun setupEventHandlers() {
        robot.kord.on<GuildChatInputCommandInteractionCreateEvent> {
            slashCommands[interaction.invokedCommandName]?.execute(this)
        }
        robot.kord.on<GuildMessageCommandInteractionCreateEvent> {
            messageCommands[interaction.invokedCommandName]?.execute(this)
        }
    }

    override suspend fun registerCommands(sCommands: Array<SlashCommand>, mCommands: Array<MessageCommand>) {
        robot.kord.createGuildApplicationCommands(Snowflake(guildId!!)) {
            sCommands.forEach { command ->
                val cmdName = "${command.name}-dev"
                input(cmdName, command.description) { command.setup(this) }
                slashCommands[cmdName] = command
            }
            robot.logger.info("Registered ${sCommands.size} slash commands")

            mCommands.forEach { command ->
                val cmdName = "${command.name}-dev"
                message(cmdName) { command.setup(this) }
                messageCommands[cmdName] = command
            }
            robot.logger.info("Registered ${messageCommands.size} message commands")
        }.collect()
    }

    companion object {
        val guildId: String? = System.getenv("DEV_GUILD_ID")
    }

}
