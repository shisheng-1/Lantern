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
package org.lanternpowered.server.inventory.container

import org.lanternpowered.api.item.inventory.container.ContainerType
import org.lanternpowered.api.item.inventory.container.ExtendedContainerType
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.inventory.container.layout.RootAnvilContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootBeaconContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootBlastFurnaceContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootBrewingContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootCartographyContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootCraftingContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootDonkeyContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootEnchantingContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootFurnaceContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootGeneric3x3ContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootGeneric9xNContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootGrindstoneContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootHopperContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootHorseContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootLecternContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootLlamaContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootLoomContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootMerchantContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootShulkerBoxContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootSmokerContainerLayout
import org.lanternpowered.server.inventory.container.layout.RootStoneCutterContainerLayout

val ContainerTypeRegistry = catalogTypeRegistry<ContainerType> {
    fun <L : ContainerLayout> register(id: String, layoutProvider: () -> L): ExtendedContainerType<L> =
            this.register(LanternContainerType(minecraftKey(id), layoutProvider))

    register("anvil", ::RootAnvilContainerLayout)
    register("beacon", ::RootBeaconContainerLayout)
    register("blast_furnace", ::RootBlastFurnaceContainerLayout)
    register("brewing_stand", ::RootBrewingContainerLayout)
    register("cartography", ::RootCartographyContainerLayout)
    register("crafting", ::RootCraftingContainerLayout)
    register("donkey") { RootDonkeyContainerLayout(false) }
    register("donkey_chested") { RootDonkeyContainerLayout(true) }
    register("enchantment", ::RootEnchantingContainerLayout)
    register("furnace", ::RootFurnaceContainerLayout)
    register("generic_3x3", ::RootGeneric3x3ContainerLayout)
    register("generic_9x1") { RootGeneric9xNContainerLayout(1) }
    register("generic_9x2") { RootGeneric9xNContainerLayout(2) }
    register("generic_9x3") { RootGeneric9xNContainerLayout(3) }
    register("generic_9x4") { RootGeneric9xNContainerLayout(4) }
    register("generic_9x5") { RootGeneric9xNContainerLayout(5) }
    register("generic_9x6") { RootGeneric9xNContainerLayout(6) }
    register("grindstone", ::RootGrindstoneContainerLayout)
    register("hopper", ::RootHopperContainerLayout)
    register("horse", ::RootHorseContainerLayout)
    register("lectern", ::RootLecternContainerLayout)
    register("llama") { RootLlamaContainerLayout(0) }
    register("llama_chested_1x3") { RootLlamaContainerLayout(1) }
    register("llama_chested_2x3") { RootLlamaContainerLayout(2) }
    register("llama_chested_3x3") { RootLlamaContainerLayout(3) }
    register("llama_chested_4x3") { RootLlamaContainerLayout(4) }
    register("llama_chested_5x3") { RootLlamaContainerLayout(5) }
    register("loom", ::RootLoomContainerLayout)
    register("merchant", ::RootMerchantContainerLayout)
    register("shulker_box", ::RootShulkerBoxContainerLayout)
    register("smoker", ::RootSmokerContainerLayout)
    register("stonecutter", ::RootStoneCutterContainerLayout)
}