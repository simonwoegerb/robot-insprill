package net.insprill.robotinsprill.configuration

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.datetime.Instant
import net.insprill.robotinsprill.codebin.BinService
import net.insprill.robotinsprill.statistic.Statistic

data class BotConfig(
    val guildId: Snowflake,
    val commands: Commands,
    val codebin: Bin,
    val audit: Audit,
    val statisticChannels: List<StatisticChannel>
) {
    data class Commands(val message: MessageCmd, val slash: Slash) {
        data class MessageCmd(val binfiles: BinFiles, val googleThat: GoogleThat) {
            data class BinFiles(val enabled: Boolean)
            data class GoogleThat(val enabled: Boolean)
        }

        data class Slash(val custom: List<CustomCommand>, val clear: Clear) {
            data class CustomCommand(
                val name: String,
                val description: String,
                val enabled: Boolean = true,
                val private: Boolean = false,
                val response: Message,
            )

            data class Clear(val enabled: Boolean, val limit: Long)
        }
    }

    data class Bin(val upload: BinService, val services: Map<BinService, List<String>>)

    data class Audit(
        val auditChannel: Snowflake,
        val ignoreChannels: List<Snowflake>,
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
                val updated: Updated
            ) {
                data class Updated(
                    val enabled: Boolean,
                    val nickname: Boolean,
                    val username: Boolean,
                    val avatar: Boolean,
                    val memberAvatar: Boolean,
                    val banner: Boolean,
                    val discriminator: Boolean,
                )
            }

            data class Messages(val deleted: Boolean, val edited: Boolean, val invitePosted: Boolean)
            data class Server(val role: Role, val channel: Channel) {
                data class Role(val created: Boolean, val deleted: Boolean)
                data class Channel(val created: Boolean, val deleted: Boolean)
            }
        }
    }

    data class StatisticChannel(
        val channelId: Snowflake,
        val format: String,
        val statistic: Statistic,
        val data: String?
    )

    data class Message(val text: String?, val embeds: List<Embed>?) {
        data class Embed(
            val title: String?,
            val description: String?,
            var url: String?,
            var timestamp: Instant?,
            var color: Color?,
            var image: String?,
            var footer: EmbedFooter?,
            var thumbnail: String?,
            var author: EmbedAuthor?,
//          var fields: MutableList<EmbedBuilder.Field>?,
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

        fun embeds(): List<EmbedBuilder>? {
            return this.embeds?.map {
                EmbedBuilder().apply {
                    title = it.title
                    description = it.description
                    url = it.url
                    timestamp = it.timestamp
                    color = it.color
                    image = it.image
                    footer = it.footer?.kord()
                    thumbnail = it.thumbnail?.let { EmbedBuilder.Thumbnail().apply { url = it } }
                    author = it.author?.kord()
//                  fields = it.fields ?: ArrayList()
                }
            }
        }
    }

    fun validate(): String? {
        if (codebin.upload == BinService.PASTEBIN && System.getenv("PASTEBIN_API_KEY") == null) {
            return "The PASTEBIN_API_KEY environment variable must be set to do uploads to pastebin!"
        }
        return null
    }
}


