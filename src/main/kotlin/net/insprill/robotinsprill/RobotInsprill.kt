package net.insprill.robotinsprill

import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import java.util.*
import mu.KLogger
import mu.KotlinLogging

suspend fun main() {
    val logger = KotlinLogging.logger("Robot Insprill")
    val kord = Kord(System.getenv("DISCORD_TOKEN"))
    RobotInsprill(logger, kord)
        .login()
}

class RobotInsprill(val logger: KLogger, val kord: Kord) {

    init {
        logger.info("Starting Robot Insprill")
    }

    suspend fun login() {
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
            logger.info("Logged into {}", kord.getSelf().tag)
        }
    }

}
