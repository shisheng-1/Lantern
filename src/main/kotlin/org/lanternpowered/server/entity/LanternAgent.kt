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
package org.lanternpowered.server.entity

import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.item.inventory.equipment.EquipmentInventory
import org.lanternpowered.server.inventory.equipment.AbstractArmorEquipable
import org.spongepowered.api.entity.ai.goal.GoalExecutor
import org.spongepowered.api.entity.ai.goal.GoalExecutorType
import org.spongepowered.api.entity.living.Agent
import java.util.Optional

open class LanternAgent(creationData: EntityCreationData) : LanternLiving(creationData), Agent, AbstractArmorEquipable {

    init {
        keyRegistry {
            register(Keys.IS_AI_ENABLED, true)
            register(Keys.TARGET_ENTITY)
        }
    }

    override fun getEquipment(): EquipmentInventory {
        TODO("Not yet implemented")
    }

    override fun <T : Agent> getGoal(type: GoalExecutorType): Optional<GoalExecutor<T>> = emptyOptional()
}
