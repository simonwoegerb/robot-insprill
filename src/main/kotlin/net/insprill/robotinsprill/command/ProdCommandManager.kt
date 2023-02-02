package net.insprill.robotinsprill.command

import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GlobalMessageCommandInteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.collect
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

    override suspend fun registerCommands(sCommands: Array<SlashCommand>, mCommands: Array<MessageCommand>) {
        robot.kord.createGlobalApplicationCommands {
            sCommands.forEach { command ->
                input(command.name, command.description) { command.setup(this) }
                slashCommands[command.name] = command
                robot.logger.info("Registered slash command '${command.name}'")
            }
            robot.logger.info("Registered ${sCommands.size} slash commands")

            mCommands.forEach { command ->
                message(command.name) { command.setup(this) }
                messageCommands[command.name] = command
                robot.logger.info("Registered message command '${command.name}'")
            }
            robot.logger.info("Registered ${messageCommands.size} message commands")
        }.collect()
    }

}

