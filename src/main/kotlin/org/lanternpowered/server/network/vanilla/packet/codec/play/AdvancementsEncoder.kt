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
import org.lanternpowered.server.network.vanilla.advancement.NetworkAdvancement
import org.lanternpowered.server.network.vanilla.packet.type.play.AdvancementsPacket

object AdvancementsEncoder : PacketEncoder<AdvancementsPacket> {

    override fun encode(ctx: CodecContext, packet: AdvancementsPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeBoolean(packet.reset)
        val added = packet.added
        buf.writeVarInt(added.size)
        for (advancement in added)
            NetworkAdvancement.write(ctx, buf, advancement)
        val removed = packet.removed
        buf.writeVarInt(removed.size)
        for (data in removed)
            buf.writeString(data)
        val progress = packet.progress
        buf.writeVarInt(progress.size)
        for ((key, value) in progress) {
            buf.writeString(key)
            buf.writeVarInt(value.size)
            for (entry1 in value.object2LongEntrySet()) {
                buf.writeString(entry1.key)
                val time = entry1.longValue
                buf.writeBoolean(time != -1L)
                if (time != -1L)
                    buf.writeLong(time)
            }
        }
        return buf
    }
}
