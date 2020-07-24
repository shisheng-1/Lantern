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

import io.netty.util.AttributeKey
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.BulkPacket
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSneakStatePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientVehicleJumpPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientMovementInputPacket

object ClientVehicleControlsCodec : PacketDecoder<Packet> {

    override fun decode(context: CodecContext, buf: ByteBuffer): Packet {
        var sideways = buf.readFloat()
        var forwards = buf.readFloat()
        val flags = buf.readByte().toInt()
        val jump = flags and 0x1 != 0
        val sneak = flags and 0x2 != 0
        val packets = mutableListOf<Packet>()
        val lastSneak = context.channel.attr(SNEAKING).getAndSet(sneak) ?: false
        if (lastSneak != sneak)
            packets.add(ClientSneakStatePacket(sneak))

        val lastJump = context.channel.attr(JUMPING).getAndSet(jump) ?: false
        if (lastJump != jump && context.channel.attr(ClientPlayerActionCodec.CANCEL_NEXT_JUMP_MESSAGE).getAndSet(false) != true)
            packets.add(ClientVehicleJumpPacket(jump, 0f))

        // The mc client already applies the sneak speed, but we want to choose it
        if (sneak) {
            sideways /= 0.3f
            forwards /= 0.3f
        }
        packets.add(ClientMovementInputPacket(forwards, sideways))
        return if (packets.size == 1) packets[0] else BulkPacket(packets)
    }

    private val SNEAKING = AttributeKey.valueOf<Boolean>("last-sneaking-state")
    private val JUMPING = AttributeKey.valueOf<Boolean>("last-jumping-state")
}
