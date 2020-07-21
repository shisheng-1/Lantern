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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.vanilla.packet.codec.handshake.HandshakeCodec;
import org.lanternpowered.server.network.vanilla.packet.handler.handshake.HandlerHandshakeIn;
import org.lanternpowered.server.network.vanilla.packet.type.handshake.HandshakePacket;

final class ProtocolHandshake extends ProtocolBase {

    ProtocolHandshake() {
        inbound().bind(HandshakeCodec.class, HandshakePacket.class)
                .bindHandler(new HandlerHandshakeIn());
    }
}
