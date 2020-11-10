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
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowPropertyPacket

object SetWindowPropertyEncoder : PacketEncoder<SetWindowPropertyPacket> {

    override fun encode(ctx: CodecContext, packet: SetWindowPropertyPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer(Byte.SIZE_BYTES + Short.SIZE_BYTES * 2)
        buf.writeByte(packet.windowId.toByte())
        buf.writeShort(packet.property.toShort())
        buf.writeShort(packet.value.toShort())
        return buf
    }
}
