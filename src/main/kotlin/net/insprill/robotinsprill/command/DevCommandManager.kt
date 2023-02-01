package net.insprill.robotinsprill.command

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import dev.kord.core.on
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

    override suspend fun registerSlash(vararg commands: SlashCommand) {
        commands.forEach { command ->
            val cmdName = "${command.name}-dev"
            robot.kord.createGuildChatInputCommand(
                Snowflake(guildId!!),
                cmdName,
                command.description
            ) { command.setup(this) }
            slashCommands[cmdName] = command
            robot.logger.info("Registered slash command '$cmdName'")
        }
    }

    override suspend fun registerMessage(vararg commands: MessageCommand) {
        commands.forEach { command ->
            val cmdName = "${command.name}-dev"
            robot.kord.createGuildMessageCommand(
                Snowflake(guildId!!),
                cmdName,
            ) { command.setup(this) }
            messageCommands[cmdName] = command
            robot.logger.info("Registered message command '$cmdName'")
        }
    }

    companion object {
        val guildId: String? = System.getenv("DEV_GUILD_ID")
    }

}
