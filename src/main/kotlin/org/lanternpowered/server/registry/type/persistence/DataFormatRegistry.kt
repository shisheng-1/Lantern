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
package org.lanternpowered.server.registry.type.persistence

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.data.persistence.HoconDataFormat
import org.lanternpowered.server.data.persistence.json.JsonDataFormat
import org.lanternpowered.server.data.persistence.nbt.NbtDataFormat
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.persistence.DataFormat

val DataFormatRegistry = catalogTypeRegistry<DataFormat> {
    register(HoconDataFormat(ResourceKey.sponge("hocon")))
    register(JsonDataFormat(ResourceKey.sponge("json")))
    register(NbtDataFormat(ResourceKey.minecraft("nbt")))
}