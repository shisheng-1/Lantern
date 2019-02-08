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
package org.lanternpowered.server.item;

import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@SuppressWarnings("unchecked")
public final class ItemProperties {

    public static final Property<Boolean> IS_ALWAYS_CONSUMABLE =
            DummyObjectProvider.createFor(Property.class, "IS_ALWAYS_CONSUMABLE");

    public static final Property<Integer> USE_COOLDOWN =
            DummyObjectProvider.createFor(Property.class, "USE_COOLDOWN");

    public static final Property<Boolean> IS_DUAL_WIELDABLE =
            DummyObjectProvider.createFor(Property.class, "IS_DUAL_WIELDABLE");

    public static final Property<Double> HEALTH_RESTORATION =
            DummyObjectProvider.createFor(Property.class, "HEALTH_RESTORATION");

    public static final Property<Integer> MAXIMUM_USE_DURATION =
            DummyObjectProvider.createFor(Property.class, "MAXIMUM_USE_DURATION");

    public static final Property<Integer> MINIMUM_USE_DURATION =
            DummyObjectProvider.createFor(Property.class, "MINIMUM_USE_DURATION");

    private ItemProperties() {
    }
}
