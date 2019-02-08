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
package org.lanternpowered.server.data.value;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.SetValue;
import org.spongepowered.api.data.value.Value;

import java.util.Set;

public class LanternMutableSetValue<E> extends LanternCollectionValue.Mutable<E, Set<E>, SetValue.Mutable<E>, SetValue.Immutable<E>>
        implements SetValue.Mutable<E> {

    public LanternMutableSetValue(Key<? extends Value<Set<E>>> key, Set<E> value) {
        super(key, value);
    }

    @Override
    public SetValue.Immutable<E> asImmutable() {
        return new LanternImmutableSetValue<>(this.key, CopyHelper.copySet(this.value));
    }

    @Override
    public SetValue.Mutable<E> copy() {
        return new LanternMutableSetValue<>(this.key, CopyHelper.copySet(this.value));
    }
}
