package net.insprill.robotinsprill.hoplite.decoder

import com.sksamuel.hoplite.ConfigFailure
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.DecoderContext
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.StringNode
import com.sksamuel.hoplite.decoder.NullHandlingDecoder
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.invalid
import dev.kord.common.Color
import kotlin.reflect.KType

class ColorDecoder : NullHandlingDecoder<Color> {
    override fun supports(type: KType): Boolean = type.classifier == Color::class

    override fun safeDecode(node: Node, type: KType, context: DecoderContext): ConfigResult<Color> {
        return when (node) {
            is StringNode -> {
                val (r, g, b) = parseHex(node.value)
                return Validated.Valid(Color(r, g, b))
            }

            else -> ConfigFailure.DecodeError(node, type).invalid()
        }
    }

    companion object {
        fun parseHex(hex: String): IntArray {
            val raw = hex.trimStart('#')
            val r = raw.substring(0, 2).toInt(16)
            val g = raw.substring(2, 4).toInt(16)
            val b = raw.substring(4, 6).toInt(16)
            return intArrayOf(r, g, b)
        }
    }
}
