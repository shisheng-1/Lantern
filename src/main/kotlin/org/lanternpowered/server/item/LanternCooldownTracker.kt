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

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.api.util.optional.emptyOptionalDouble
import org.lanternpowered.api.util.optional.emptyOptionalInt
import org.lanternpowered.api.util.optional.asOptionalDouble
import org.lanternpowered.api.util.optional.asOptionalInt
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCooldownPacket
import org.spongepowered.api.entity.living.player.CooldownTracker
import java.util.OptionalDouble
import java.util.OptionalInt

class LanternCooldownTracker(private val player: LanternPlayer) : CooldownTracker {

    private val map = Object2LongOpenHashMap<ItemType>().apply { this.defaultReturnValue(-1L) }

    override fun setCooldown(itemType: ItemType, ticks: Int): Boolean {
        var cooldown = ticks
        val current = LanternGame.currentTimeTicks()
        val time = this.map.getLong(itemType) - current
        if (time <= 0 && cooldown <= 0) {
            return false
        }
        val optionalStartCooldown = if (time <= 0) emptyOptionalInt() else time.toInt().asOptionalInt()
        val event = LanternEventFactory.createCooldownEventSet(CauseStack.currentCause,
                cooldown, cooldown, itemType, this.player, optionalStartCooldown)
        EventManager.post(event)
        if (event.isCancelled)
            return false
        cooldown = event.newCooldown
        when {
            cooldown > 0 -> {
                this.map[itemType] = current + cooldown
            }
            time > 0 -> {
                this.map.removeLong(itemType)
                cooldown = 0
            }
            else -> cooldown = -1
        }
        if (cooldown >= 0) {
            this.player.connection.send(SetCooldownPacket(itemType, cooldown))
        }
        return true
    }

    override fun resetCooldown(itemType: ItemType): Boolean {
        return this.setCooldown(itemType, 0)
    }

    override fun getCooldown(itemType: ItemType): OptionalInt {
        val time = this.map.getLong(itemType)
        if (time != -1L) {
            val current = LanternGame.currentTimeTicks()
            if (time > current)
                return (time - current).toInt().asOptionalInt()
        }
        return emptyOptionalInt()
    }

    override fun hasCooldown(itemType: ItemType): Boolean {
        val time = this.map.getLong(itemType)
        if (time != -1L) {
            val current = LanternGame.currentTimeTicks()
            if (time > current)
                return true
        }
        return false
    }

    override fun getFractionRemaining(type: ItemType): OptionalDouble {
        // TODO: Properly implement this
        return if (this.hasCooldown(type)) 1.0.asOptionalDouble() else emptyOptionalDouble()
    }

    fun process() {
        val current = LanternGame.currentTimeTicks()
        this.map.object2LongEntrySet().removeIf { entry ->
            if (entry.longValue < current) {
                val event = LanternEventFactory.createCooldownEventEnd(
                        CauseStack.currentCause, entry.key, this.player)
                EventManager.post(event)
                true
            } else false
        }
    }
}
