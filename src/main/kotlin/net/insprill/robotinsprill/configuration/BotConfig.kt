package net.insprill.robotinsprill.configuration

import net.insprill.robotinsprill.codebin.BinService

data class BotConfig(val commands: Commands, val codebin: Bin) {
    data class Commands(val message: Message) {
        data class Message(val binfile: BinFile) {
            data class BinFile(val codebin: BinService)
        }
    }

    data class Bin(val services: Map<BinService, List<String>>)

    fun validate(): String? {
        if (commands.message.binfile.codebin == BinService.PASTEBIN && System.getenv("PASTEBIN_KEY") == null) {
            return "The PASTEBIN_KEY environment variable must be set to do uploads to pastebin!"
        }
        return null
    }
}


