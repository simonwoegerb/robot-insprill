package net.insprill.robotinsprill.configuration

data class BotConfig(val commands: Commands) {
    data class Commands(val message: Message) {
        data class Message(val binfile: BinFile) {
            data class BinFile(val binUrl: String)
        }
    }
}


