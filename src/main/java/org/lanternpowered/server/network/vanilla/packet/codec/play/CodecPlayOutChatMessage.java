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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.MessagePlayOutChatPacket;

public final class CodecPlayOutChatMessage implements Codec<MessagePlayOutChatPacket> {

    @SuppressWarnings("deprecation")
    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutChatPacket packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        context.write(buf, ContextualValueTypes.TEXT, packet.getMessage());
        buf.writeByte((byte) packet.getType().ordinal());
        return buf;
    }
}