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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.math.vector.Vector3d

data class ClientPlayerVehicleMovementPacket(
        val position: Vector3d,
        val yaw: Float,
        val pitch: Float
) : Packet