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
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket

object SetWindowSlotEncoder : PacketEncoder<SetWindowSlotPacket> {

    override fun encode(ctx: CodecContext, packet: SetWindowSlotPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeByte(packet.window.toByte())
        buf.writeShort(packet.index.toShort())
        NetworkItemStack.write(ctx, buf, packet.item)
        return buf
    }
}
