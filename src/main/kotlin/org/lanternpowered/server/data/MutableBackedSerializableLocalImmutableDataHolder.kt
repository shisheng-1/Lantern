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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.spongepowered.api.data.SerializableDataHolder
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView

abstract class MutableBackedSerializableLocalImmutableDataHolder<I : SerializableDataHolder.Immutable<I>, M : SerializableLocalMutableDataHolder>(
        backingDataHolder: M
) : MutableBackedCopyableLocalImmutableDataHolder<I, M>(backingDataHolder), SerializableDataHolder.Immutable<I> {

    override fun validateRawData(container: DataView): Boolean =
            this.backingDataHolder.validateRawData(container)

    override fun withRawData(container: DataView): I {
        val copy = this.backingDataHolder.copy() as M
        copy.setRawData(container)
        return withBacking(copy)
    }

    override fun toContainer(): DataContainer =
            this.backingDataHolder.toContainer()

    override fun getContentVersion(): Int =
            this.backingDataHolder.contentVersion
}
