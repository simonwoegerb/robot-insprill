package net.insprill.robotinsprill.command

import net.insprill.robotinsprill.command.message.MessageCommand
import net.insprill.robotinsprill.command.slash.SlashCommand

abstract class CommandManager {

    protected val slashCommands = HashMap<String, SlashCommand>()
    protected val messageCommands = HashMap<String, MessageCommand>()

    abstract fun setupEventHandlers()

    abstract suspend fun registerSlash(vararg commands: SlashCommand)

    abstract suspend fun registerMessage(vararg commands: MessageCommand)

}
