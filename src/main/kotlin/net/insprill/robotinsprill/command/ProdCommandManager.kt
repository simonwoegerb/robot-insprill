package net.insprill.robotinsprill.command

import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalMessageCommandInteractionCreateEvent
import dev.kord.core.on
import net.insprill.robotinsprill.RobotInsprill
import net.insprill.robotinsprill.command.message.MessageCommand
import net.insprill.robotinsprill.command.slash.SlashCommand

class ProdCommandManager(private val robot: RobotInsprill) : CommandManager() {

    override fun setupEventHandlers() {
        robot.kord.on<GlobalChatInputCommandInteractionCreateEvent> {
            slashCommands[interaction.invokedCommandName]?.execute(this)
        }
        robot.kord.on<GlobalMessageCommandInteractionCreateEvent> {
            messageCommands[interaction.invokedCommandName]?.execute(this)
        }
    }

    override suspend fun registerSlash(vararg commands: SlashCommand) {
        commands.forEach { command ->
            robot.kord.createGlobalChatInputCommand(command.name, command.description) { command.setup(this) }
            slashCommands[command.name] = command
            robot.logger.info("Registered slash command '${command.name}'")
        }
    }

    override suspend fun registerMessage(vararg commands: MessageCommand) {
        commands.forEach { command ->
            robot.kord.createGlobalMessageCommand(command.name) { command.setup(this) }
            messageCommands[command.name] = command
            robot.logger.info("Registered message command '${command.name}'")
        }
    }

}

