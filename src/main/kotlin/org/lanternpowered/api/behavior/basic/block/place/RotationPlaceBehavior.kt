/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.api.behavior.basic.block.place

import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorContextKeys
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.PlaceBlockBehaviorBase
import org.lanternpowered.api.block.BlockSnapshotBuilder
import org.lanternpowered.api.data.key.Keys
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.behavior.ContextKeys
import org.spongepowered.api.util.Direction

/**
 * A behavior that rotates the blocks based on the
 * direction the player is looking.
 *
 * @property horizontalOnly Whether the block should only be rotated in the horizontal plane (around the y axis)
 */
class RotationPlaceBehavior(
        private val horizontalOnly: Boolean = false
) : PlaceBlockBehaviorBase {

    override fun apply(type: BehaviorType, ctx: BehaviorContext, placed: MutableList<BlockSnapshotBuilder>): Boolean {
        val player = ctx[BehaviorContextKeys.PLAYER]
        val face = if (player != null) {
            if (!this.horizontalOnly && player.position.y - ctx.requireContext(ContextKeys.BLOCK_LOCATION).blockPosition.y >= 0.5) {
                player.getDirection(Direction.Division.CARDINAL)
            } else {
                player.getHorizontalDirection(Direction.Division.CARDINAL)
            }.opposite
        } else Direction.NORTH
        for (builder in placed) {
            val state = builder.blockState
            builder.blockState = state.with(Keys.DIRECTION, face).orElse(state)
        }
        return true
    }
}
