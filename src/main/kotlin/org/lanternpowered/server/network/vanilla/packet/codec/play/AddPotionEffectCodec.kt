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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.AddPotionEffectPacket
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry

object AddPotionEffectCodec : PacketEncoder<AddPotionEffectPacket> {

    override fun encode(context: CodecContext, packet: AddPotionEffectPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeByte(PotionEffectTypeRegistry.getId(packet.type).toByte())
        buf.writeByte(packet.amplifier.toByte())
        buf.writeVarInt(packet.duration)
        var flags = 0
        if (packet.isAmbient)
            flags += 0x1
        if (packet.showParticles)
            flags += 0x2
        buf.writeByte(flags.toByte())
        return buf
    }
}
