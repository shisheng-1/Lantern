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

import org.spongepowered.api.block.BlockState;

import java.util.function.Function;

public class SimplePropertyProvider<V> extends SimpleObjectProvider<V> implements PropertyProvider<V> {

    public SimplePropertyProvider(Function<BlockState, V> provider) {
        super(provider);
    }
}
