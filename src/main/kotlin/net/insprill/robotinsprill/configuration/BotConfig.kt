package net.insprill.robotinsprill.configuration

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.datetime.Instant
import net.insprill.robotinsprill.codebin.BinService
import net.insprill.robotinsprill.statistic.Statistic

data class BotConfig(
    val commands: Commands,
    val codebin: Bin,
    val audit: Audit,
    val statisticChannels: List<StatisticChannel>
) {
    data class Commands(val slash: Slash) {
        data class Slash(val custom: List<CustomCommand>) {
            data class CustomCommand(
                val name: String,
                val description: String,
                val private: Boolean = false,
                val message: String?,
                val embed: CustomCommandEmbed?
            ) {
                data class CustomCommandEmbed(
                    val title: String?,
                    val description: String?,
                    var url: String?,
                    var timestamp: Instant?,
                    var color: Color?,
                    var image: String?,
                    var footer: EmbedFooter?,
                    var thumbnail: String?,
                    var author: EmbedAuthor?,
//                    var fields: MutableList<EmbedBuilder.Field>?,
                ) {
                    data class EmbedFooter(val text: String, val icon: String?) {
                        fun kord(): EmbedBuilder.Footer {
                            return EmbedBuilder.Footer().also {
                                it.text = this.text
                                it.icon = this.icon
                            }
                        }
                    }

                    data class EmbedAuthor(val name: String?, val icon: String?, val url: String?) {
                        fun kord(): EmbedBuilder.Author {
                            return EmbedBuilder.Author().also {
                                it.name = this.name
                                it.icon = this.icon
                                it.url = this.url
                            }
                        }
                    }
                }
            }
        }
    }

    data class Bin(val upload: BinService, val services: Map<BinService, List<String>>)

    data class Audit(
        val auditChannels: Map<Snowflake, Snowflake>,
        val ignoreChannels: Map<Snowflake, Snowflake>,
        val logBots: Boolean,
        val events: Events
    ) {
        data class Events(val members: Members, val messages: Messages, val server: Server) {
            data class Members(
                val banned: Boolean,
                val unbanned: Boolean,
                val joined: Boolean,
                val left: Boolean,
                val voice: Boolean,
            )

            data class Messages(val deleted: Boolean, val edited: Boolean, val invitePosted: Boolean)
            data class Server(val role: Role, val channel: Channel) {
                data class Role(val created: Boolean, val deleted: Boolean)
                data class Channel(val created: Boolean, val deleted: Boolean)
            }
        }
    }

    data class StatisticChannel(
        val channelId: Pair<Snowflake, Snowflake>,
        val format: String,
        val statistic: Statistic,
        val data: String?
    )

    fun validate(): String? {
        if (codebin.upload == BinService.PASTEBIN && System.getenv("PASTEBIN_KEY") == null) {
            return "The PASTEBIN_KEY environment variable must be set to do uploads to pastebin!"
        }
        return null
    }
}


