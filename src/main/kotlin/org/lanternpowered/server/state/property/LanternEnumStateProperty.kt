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
package org.lanternpowered.server.state.property

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.state.identityStateKeyValueTransformer
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.EnumStateProperty
import java.util.Optional

internal class LanternEnumStateProperty<E : Enum<E>>(
        key: CatalogKey, valueClass: Class<E>, possibleValues: ImmutableSet<E>, valueKey: Key<out Value<E>>
) : AbstractStateProperty<E, E>(key, valueClass, possibleValues, valueKey, identityStateKeyValueTransformer()), EnumStateProperty<E> {

    override fun parseValue(value: String): Optional<E> {
        for (enumValue in valueClass.enumConstants) {
            if (enumValue.name.equals(value, ignoreCase = true)) {
                return enumValue.optional()
            }
        }
        return emptyOptional()
    }
}
