/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry

class CodecPlayOutScoreboardObjective : Codec<MessagePlayOutScoreboardObjective> {

    override fun encode(context: CodecContext, message: MessagePlayOutScoreboardObjective): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeString(message.objectiveName)
        if (message is MessagePlayOutScoreboardObjective.CreateOrUpdate) {
            buf.writeByte((if (message is MessagePlayOutScoreboardObjective.Create) 0 else 2).toByte())
            context.write(buf, ContextualValueTypes.TEXT, message.displayName)
            buf.writeVarInt(ObjectiveDisplayModeRegistry.getId(message.displayMode))
        } else {
            buf.writeByte(1.toByte())
        }
        return buf
    }
}
