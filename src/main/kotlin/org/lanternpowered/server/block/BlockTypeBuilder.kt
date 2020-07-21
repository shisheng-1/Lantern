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
package org.lanternpowered.server.block

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.resourceKeyOf
import org.lanternpowered.api.util.math.times
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.util.AABB
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.block.state.BlockStateProperties
import org.lanternpowered.server.item.ItemTypeBuilder
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityType
import org.spongepowered.api.block.entity.BlockEntityTypes
import org.spongepowered.api.state.StateProperty
import org.spongepowered.math.vector.Vector3d

val testBlockType = blockTypeOf(resourceKeyOf("namespace", "value")) {
    name("Test Block")
    stateProperty(BlockStateProperties.IS_WET)
    properties {
        register(BlockProperties.BLOCK_SOUND_GROUP, BlockSoundGroups.GLASS)
        register(BlockProperties.BLAST_RESISTANCE, 10.2)
        forStates {
            registerProvider(BlockProperties.FLAMMABLE_INFO) {
                get {
                    if (getStateProperty(BlockStateProperties.IS_WET).orElse(false)) {
                        null
                    } else {
                        FlammableInfo(1, 1)
                    }
                }
                get { direction ->
                    FlammableInfo(1, 1)
                }
            }
        }
    }
    blockEntity(BlockEntityTypes.BANNER)
    collisionBox {
        if (getStateProperty(BlockStateProperties.IS_WET).orElse(false)) {
            AABB(Vector3d.ZERO, Vector3d.ONE * 2)
        } else {
            AABB(Vector3d.ZERO, Vector3d.ONE)
        }
    }
    itemType()
    itemType {
        name("Something else")
    }
    behaviors {

    }
}

fun blockTypeOf(key: ResourceKey, fn: BlockTypeBuilder.() -> Unit): BlockType {
    TODO()
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class BlockTypeBuilderDsl

@BlockTypeBuilderDsl
interface BlockTypeBuilder {

    fun name(name: String)
    fun name(name: Translation)

    /**
     * Adds a single [StateProperty].
     */
    fun stateProperty(stateProperty: StateProperty<*>)

    /**
     * Adds multiple [StateProperty]s.
     */
    fun stateProperties(first: StateProperty<*>, vararg more: StateProperty<*>)

    /**
     * Applies a new default [BlockState].
     */
    fun defaultState(fn: @BlockTypeBuilderDsl BlockState.() -> BlockState)

    /**
     * Applies a [BlockEntity] to the block type.
     */
    fun blockEntity(blockEntityType: BlockEntityType)

    /**
     * Applies the selection bounding box.
     */
    fun selectionBox(selectionBox: AABB?)

    /**
     * Applies the selection bounding box based on the [BlockState].
     */
    fun selectionBox(fn: @BlockTypeBuilderDsl BlockState.() -> AABB)

    /**
     * Applies the collision bounding box.
     */
    fun collisionBox(collisionBox: AABB?)

    /**
     * Applies the collision bounding box based on the [BlockState].
     */
    fun collisionBox(fn: @BlockTypeBuilderDsl BlockState.() -> AABB)

    /**
     * Applies the collision bounding boxes.
     */
    fun collisionBoxes(collisionBox: Collection<AABB>)

    /**
     * Applies the collision bounding boxes based on the [BlockState].
     */
    fun collisionBoxes(fn: @BlockTypeBuilderDsl BlockState.() -> Collection<AABB>)

    /**
     * Applies properties to the [BlockType].
     */
    fun properties(fn: BlockTypePropertyRegistryBuilder.() -> Unit)

    /**
     * Applies behaviors to the [BlockType].
     */
    fun behaviors(fn: @BlockTypeBuilderDsl MutableBehaviorPipeline<Behavior>.() -> Unit)

    /**
     * Enables a item type for the block type and allows the item type to be modified.
     */
    fun itemType(fn: @BlockTypeBuilderDsl ItemTypeBuilder.() -> Unit = {})
}

@BlockTypeBuilderDsl
abstract class BlockTypePropertyRegistryBuilder : PropertyRegistry<BlockType>() {

    /**
     * Applies properties to a block state, applied properties here will
     * override the default properties provided by [BlockType].
     */
    abstract fun forStates(fn: PropertyRegistry<BlockState>.() -> Unit)
}