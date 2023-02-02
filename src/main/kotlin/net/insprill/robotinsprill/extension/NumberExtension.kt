package net.insprill.robotinsprill.extension

fun Long.pretty(): String {
    return when {
        this >= 100000000 -> "%.1fM".format(this / 1000000.0)
        this >= 1000000 -> "%.2fM".format(this / 1000000.0)
        this >= 100000 -> "%.1fK".format(this / 1000.0)
        this >= 10000 -> "%.2fK".format(this / 1000.0)
        else -> this.toString()
    }
}
