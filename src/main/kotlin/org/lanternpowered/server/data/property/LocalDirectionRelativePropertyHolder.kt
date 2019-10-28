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
package org.lanternpowered.server.data.property

import org.spongepowered.api.data.property.Property
import org.spongepowered.api.util.Direction
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

interface LocalDirectionRelativePropertyHolder : LocalPropertyHolder, DirectionRelativePropertyHolderBase {

    override val propertyRegistry: PropertyRegistry<out LocalDirectionRelativePropertyHolder>

    @JvmDefault
    override fun <V : Any> getProperty(direction: Direction, property: Property<V>): Optional<V> {
        val value = this.propertyRegistry.getProvider(property).getFor(this, direction)
        if (value.isPresent) {
            return value
        }
        return super<DirectionRelativePropertyHolderBase>.getProperty(direction, property)
    }

    @JvmDefault
    override fun getIntProperty(direction: Direction, property: Property<Int>): OptionalInt {
        val value = this.propertyRegistry.getIntProvider(property).getIntFor(this, direction)
        if (value.isPresent) {
            return value
        }
        return super<DirectionRelativePropertyHolderBase>.getIntProperty(direction, property)
    }

    @JvmDefault
    override fun getDoubleProperty(direction: Direction, property: Property<Double>): OptionalDouble {
        val value = this.propertyRegistry.getDoubleProvider(property).getDoubleFor(this, direction)
        if (value.isPresent) {
            return value
        }
        return super<DirectionRelativePropertyHolderBase>.getDoubleProperty(direction, property)
    }
}
