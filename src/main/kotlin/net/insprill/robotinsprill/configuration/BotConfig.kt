package net.insprill.robotinsprill.configuration

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.datetime.Instant
import net.insprill.robotinsprill.codebin.BinService

data class BotConfig(val commands: Commands, val codebin: Bin) {
    data class Commands(val message: Message, val slash: Slash) {
        data class Message(val binfiles: BinFiles) {
            data class BinFiles(val codebin: BinService)
        }

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

    data class Bin(val services: Map<BinService, List<String>>)

    fun validate(): String? {
        if (commands.message.binfiles.codebin == BinService.PASTEBIN && System.getenv("PASTEBIN_KEY") == null) {
            return "The PASTEBIN_KEY environment variable must be set to do uploads to pastebin!"
        }
        return null
    }
}


