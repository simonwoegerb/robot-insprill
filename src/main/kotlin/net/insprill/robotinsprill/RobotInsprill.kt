package net.insprill.robotinsprill

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.fp.getOrElse
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import java.io.File
import java.util.*
import kotlin.system.exitProcess
import mu.KLogger
import mu.KotlinLogging
import net.insprill.robotinsprill.command.CommandManager
import net.insprill.robotinsprill.command.DevCommandManager
import net.insprill.robotinsprill.command.ProdCommandManager
import net.insprill.robotinsprill.command.message.BinFile
import net.insprill.robotinsprill.configuration.BotConfig

suspend fun main() {
    val logger = KotlinLogging.logger("Robot Insprill")
    val kord = Kord(System.getenv("DISCORD_TOKEN"))
    RobotInsprill(logger, kord)
        .registerCommands()
        .login()
}

class RobotInsprill(val logger: KLogger, val kord: Kord) {

    private val commandManager: CommandManager
    private val config: BotConfig

    init {
        logger.info("Starting Robot Insprill")

        val configLoader = ConfigLoaderBuilder.default()
            .addFileSource(File("config.yml"))
            .build()
            .loadConfig<BotConfig>()

        config = configLoader.getOrElse {
            logger.error("Failed to load configuration file: ${it.description()}")
            exitProcess(1)
        }

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
