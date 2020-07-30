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
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifySignPacket

object ClientChangeSignCodec : PacketDecoder<ClientModifySignPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientModifySignPacket {
        val position = buf.readPosition()
        val lines = Array(ClientModifySignPacket.EXPECTED_LINES_SIZE) { "" }
        for (i in lines.indices)
            lines[i] = buf.readLimitedString(384)
        return ClientModifySignPacket(position, lines)
    }
}