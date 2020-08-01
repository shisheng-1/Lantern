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
package org.lanternpowered.server.item

import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl
import org.lanternpowered.server.data.LocalKeyRegistry

class LanternItemTypeBuilder : ItemTypeBuilder {

    private var nameFunction: (ItemStack.() -> Text)? = null
    private var maxStackQuantity = 64
    private val keysFunctions = mutableListOf<LocalKeyRegistry<ItemType>.() -> Unit>()
    private val stackKeysFunctions = mutableListOf<LocalKeyRegistry<ItemStack>.() -> Unit>()
    private val behaviorsBuilderFunctions = mutableListOf<MutableBehaviorPipeline<Behavior>.() -> Unit>()

    /**
     * The block type bound to this item type, if any.
     */
    internal var blockType: BlockType? = null

    override fun name(fn: ItemStack.() -> Text) {
        this.nameFunction = fn
    }

    override fun name(name: String) {
        name(textOf(name))
    }

    override fun name(name: Text) {
        name { name }
    }

    override fun maxStackQuantity(quantity: Int) {
        check(quantity > 0) { "The max stack quantity must be greater than 0" }
        this.maxStackQuantity = quantity
    }

    override fun keys(fn: LocalKeyRegistry<ItemType>.() -> Unit) {
        this.keysFunctions += fn
    }

    override fun stackKeys(fn: LocalKeyRegistry<ItemStack>.() -> Unit) {
        this.stackKeysFunctions += fn
    }

    override fun behaviors(fn: MutableBehaviorPipeline<Behavior>.() -> Unit) {
        this.behaviorsBuilderFunctions += fn
    }

    fun build(key: NamespacedKey): ItemType {
        var nameFunction = this.nameFunction
        if (nameFunction == null) {
            val def = translatableTextOf("item.${key.namespace}.${key.value}")
            nameFunction = { def }
        }

        val keyRegistry = LocalKeyRegistry.of<ItemType>()
        for (fn in this.keysFunctions)
            keyRegistry.fn()

        // Already create the key registry, this can be copied
        // to every item stack later, instead of reapplying every function
        val stackKeyRegistry = LocalKeyRegistry.of<ItemStack>()
        for (fn in this.stackKeysFunctions)
            stackKeyRegistry.fn()

        val behaviorPipeline = MutableBehaviorPipelineImpl(Behavior::class.java, mutableListOf())
        for (fn in this.behaviorsBuilderFunctions)
            behaviorPipeline.fn()

        return LanternItemType(key, nameFunction, this.blockType, this.maxStackQuantity,
                keyRegistry, stackKeyRegistry, behaviorPipeline)
    }
}
