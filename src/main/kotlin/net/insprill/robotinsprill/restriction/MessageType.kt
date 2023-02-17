package net.insprill.robotinsprill.restriction

import dev.kord.core.entity.Message

enum class MessageType(val func: (Message) -> Boolean) {
    TEXT({ message ->
        message.content.isNotEmpty() && !LINK.func.invoke(message)
    }),
    IMAGE({ message ->
        message.attachments.isNotEmpty() && message.attachments.all { it.isImage }
    }),
    LINK({ message ->
        message.content.contains("http")
    }),
    VIDEO({ message ->
        message.attachments.isNotEmpty() && message.attachments.all {
            listOf(
                "video/mp4",
                "video/webm",
                "video/mov"
            ).contains(it.contentType)
        }
    }),
    ATTACHMENT({ message ->
        !(IMAGE.func.invoke(message) || VIDEO.func.invoke(message)) && message.attachments.isNotEmpty()
    }),
}