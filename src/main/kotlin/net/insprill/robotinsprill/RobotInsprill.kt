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
import net.insprill.robotinsprill.command.message.Google
import net.insprill.robotinsprill.command.slash.CustomCommand
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
    val config: BotConfig

    init {
        logger.info("Starting Robot Insprill")

        logger.info("Parsing configuration file")
        config = ConfigLoaderBuilder.default()
            .addFileSource(File("config.yml"))
            .build()
            .loadConfig<BotConfig>()
            .getOrElse {
                logger.error(it.description())
                exitProcess(1)
            }

        logger.info("Validating configuration file")
        config.validate()?.let {
            logger.error(it)
            exitProcess(1)
        }

        logger.info("Using the ${if (DevCommandManager.guildId != null) "development" else "production"} command manager")
        commandManager = if (DevCommandManager.guildId != null) {
            DevCommandManager(this)
        } else {
            ProdCommandManager(this)
        }
    }

    suspend fun registerCommands() = apply {
        logger.info("Setting up command handlers")
        commandManager.setupEventHandlers()

        logger.info("Registering message commands")
        commandManager.registerMessage(
            BinFile(this),
            Google()
        )

        logger.info("Registering slash commands")
        commandManager.registerSlash(
            *CustomCommand.buildCommandArray(this.config)
        )
    }

    suspend fun login() {
        logger.info("Logging in")
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
            logger.info("Logged into {}", kord.getSelf().tag)
        }
    }

}
