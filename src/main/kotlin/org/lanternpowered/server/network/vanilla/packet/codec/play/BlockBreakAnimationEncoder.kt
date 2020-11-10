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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockBreakAnimationPacket

object BlockBreakAnimationEncoder : PacketEncoder<BlockBreakAnimationPacket> {

    override fun encode(ctx: CodecContext, packet: BlockBreakAnimationPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.id)
        buf.writeBlockPosition(packet.position)
        // Make sure that the state fits in the byte
        val state = packet.state
        buf.writeByte((if (state in 0..9) state else 10).toByte())
        return buf
    }
}
