package net.insprill.robotinsprill.extension

import dev.kord.common.entity.ChannelType

fun ChannelType.prettyName(): String {
    return when (this.value) {
        0 -> "Text"
        1 -> "PM"
        2 -> "Voice"
        3 -> "Group PM"
        4 -> "Category"
        5 -> "News"
        10 -> "News Thread"
        11 -> "Public Thread"
        12 -> "Private Thread"
        13 -> "Stage"
        14 -> "Directory"
        else -> "Unknown"
    }
}
