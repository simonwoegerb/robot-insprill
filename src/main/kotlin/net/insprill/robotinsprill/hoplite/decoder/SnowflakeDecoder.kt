package net.insprill.robotinsprill.hoplite.decoder

import com.sksamuel.hoplite.ConfigFailure
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.DecoderContext
import com.sksamuel.hoplite.LongNode
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.StringNode
import com.sksamuel.hoplite.decoder.NullHandlingDecoder
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.invalid
import dev.kord.common.entity.Snowflake
import kotlin.reflect.KType

class SnowflakeDecoder : NullHandlingDecoder<Snowflake> {

    override fun supports(type: KType): Boolean = type.classifier == Snowflake::class

    override fun safeDecode(node: Node, type: KType, context: DecoderContext): ConfigResult<Snowflake> {
        return when (node) {
            is StringNode -> {
                return Validated.Valid(Snowflake(node.value))
            }

            is LongNode -> {
                return Validated.Valid(Snowflake(node.value))
            }

            else -> ConfigFailure.DecodeError(node, type).invalid()
        }
    }

}
