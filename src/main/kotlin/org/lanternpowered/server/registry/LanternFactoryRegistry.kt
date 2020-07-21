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
package org.lanternpowered.server.registry

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.registry.DuplicateRegistrationException
import org.lanternpowered.api.registry.FactoryRegistry
import org.lanternpowered.api.registry.UnknownTypeException

object LanternFactoryRegistry : FactoryRegistry {

    private val factories = mutableMapOf<Class<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> provideFactory(clazz: Class<T>): T =
            this.factories[clazz] as? T ?: throw UnknownTypeException("There's no factory registered with the type ${clazz.simpleName}.")

    fun register(factory: Any) {
        val factoryTypes = TypeToken.of(factory.javaClass).types.rawTypes()
                .filterNot { it as Class<*> == Object::class.java }
                .filter { it.isInterface }
        for (factoryType in factoryTypes)
            this.factories.putIfAbsent(factoryType, factory)
    }

    fun <T : Any> register(factoryClass: Class<T>, factory: T): T {
        val old = this.factories.putIfAbsent(factoryClass, factory)
        if (old != null)
            throw DuplicateRegistrationException("There's already a factory registered fot the type: $factoryClass")
        return factory
    }
}