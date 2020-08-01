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
package org.lanternpowered.server.block.provider.property;

import org.lanternpowered.server.block.provider.ConstantBlockObjectProvider;

public class ConstantPropertyProvider<V> extends ConstantBlockObjectProvider<V> implements PropertyProvider<V> {

    public ConstantPropertyProvider(V value) {
        super(value);
    }
}
