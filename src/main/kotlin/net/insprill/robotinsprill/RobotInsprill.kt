package net.insprill.robotinsprill

import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import java.util.*
import mu.KLogger
import mu.KotlinLogging
import net.insprill.robotinsprill.command.CommandManager
import net.insprill.robotinsprill.command.DevCommandManager
import net.insprill.robotinsprill.command.ProdCommandManager
import net.insprill.robotinsprill.command.message.BinFile

suspend fun main() {
    val logger = KotlinLogging.logger("Robot Insprill")
    val kord = Kord(System.getenv("DISCORD_TOKEN"))
    RobotInsprill(logger, kord)
        .registerCommands()
        .login()
}

class RobotInsprill(val logger: KLogger, val kord: Kord) {

    private val commandManager: CommandManager

    init {
        logger.info("Starting Robot Insprill")

        commandManager = if (DevCommandManager.guildId != null) {
            DevCommandManager(this)
        } else {
            ProdCommandManager(this)
        }
    }

    suspend fun registerCommands() = apply {
        commandManager.setupEventHandlers()
        commandManager.registerMessage(
            BinFile()
        )
    }

    suspend fun login() {
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
            logger.info("Logged into {}", kord.getSelf().tag)
        }
    }

}
