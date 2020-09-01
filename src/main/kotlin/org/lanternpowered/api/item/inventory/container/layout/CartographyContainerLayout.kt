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
package org.lanternpowered.api.item.inventory.container.layout

/**
 * Represents the top container layout of a cartography table.
 */
interface CartographyContainerLayout : ContainerLayout {

    /**
     * The map input slot.
     */
    val map: ContainerSlot

    /**
     * The paper input slot.
     */
    val paper: ContainerSlot

    /**
     * The output slot.
     */
    val output: ContainerSlot
}