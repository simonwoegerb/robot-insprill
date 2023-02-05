package net.insprill.robotinsprill.extension

import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.thread.ThreadChannel

suspend fun MessageChannel.parentOrSelf(): MessageChannel {
    return if (this is ThreadChannel) this.parent.asChannel() else this
}
