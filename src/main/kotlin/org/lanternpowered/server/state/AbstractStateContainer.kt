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
package org.lanternpowered.server.state

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Lists
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.namespacedKey
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.util.collections.immutableMapBuilderOf
import org.lanternpowered.api.util.collections.immutableMapOf
import org.lanternpowered.api.util.collections.immutableSetBuilderOf
import org.lanternpowered.api.util.collections.immutableSetOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateContainer
import org.spongepowered.api.state.StateProperty
import java.util.LinkedHashMap
import java.util.Optional

abstract class AbstractStateContainer<S : State<S>>(
        baseKey: NamespacedKey, stateProperties: Iterable<StateProperty<*>>, constructor: (StateBuilder<S>) -> S
) : StateContainer<S> {

    // The lookup to convert between key <--> state property
    val keysToProperty: ImmutableMap<Key<out Value<*>>, StateProperty<*>>
    private val validStates: ImmutableList<S>

    init {
        val properties = stateProperties.toMutableList()
        properties.sortBy { property -> property.getName() }

        val keysToPropertyBuilder = immutableMapBuilderOf<Key<out Value<*>>, StateProperty<*>>()
        for (property in properties)
            keysToPropertyBuilder.put((property as IStateProperty<*,*>).valueKey, property)
        this.keysToProperty = keysToPropertyBuilder.build()

        val cartesianProductInput = properties.map { property ->
            property.getPossibleValues().map { comparable ->
                @Suppress("UNCHECKED_CAST")
                val valueKey = (property as IStateProperty<*,*>).valueKey as Key<Value<Any>>
                val value = Value.immutableOf(valueKey, comparable as Any).asImmutable()
                Triple(property, comparable, value)
            }
        }
        val cartesianProduct = Lists.cartesianProduct(cartesianProductInput)
        val stateBuilders = mutableListOf<LanternStateBuilder<S>>()

        // A map with as the key the property values map and as value the state
        val stateByValuesMap = LinkedHashMap<Map<*, *>, S>()

        for ((internalId, list) in cartesianProduct.withIndex()) {
            val stateValuesBuilder = immutableMapBuilderOf<StateProperty<*>, Comparable<*>>()
            val immutableValuesBuilder = immutableSetBuilderOf<Value.Immutable<*>>()

            for ((property, comparable, value) in list) {
                stateValuesBuilder.put(property, comparable)
                immutableValuesBuilder.add(value)
            }

            val stateValues = stateValuesBuilder.build()
            val immutableValues = immutableValuesBuilder.build()
            val key = this.buildKey(baseKey, stateValues)
            val dataContainer = this.buildDataContainer(baseKey, stateValues)

            @Suppress("LeakingThis")
            stateBuilders += LanternStateBuilder(key, dataContainer, this, stateValues, immutableValues, internalId)
        }

        // There are no properties, so just add
        // the single state of this container
        if (properties.isEmpty()) {
            val dataContainer = this.buildDataContainer(baseKey, immutableMapOf())

            @Suppress("LeakingThis")
            stateBuilders += LanternStateBuilder(baseKey, dataContainer, this, immutableMapOf(), immutableSetOf(), 0)
        }

        this.validStates = stateBuilders.map {
            val state = constructor(it)
            stateByValuesMap[it.stateValues] = state
            state
        }.toImmutableList()

        for (state in this.validStates) {
            val tableBuilder = ImmutableTable.builder<StateProperty<*>, Comparable<*>, S>()
            for (property in properties) {
                @Suppress("UNCHECKED_CAST")
                property as StateProperty<Comparable<Comparable<*>>>
                for (value in property.possibleValues) {
                    if (value == state.getStateProperty(property).get())
                        continue
                    val valueByProperty = HashMap<StateProperty<*>, Any>(state.statePropertyMap)
                    valueByProperty[property] = value
                    tableBuilder.put(property, value, checkNotNull(stateByValuesMap[valueByProperty]))
                }
            }
            @Suppress("UNCHECKED_CAST")
            (state as AbstractState<S,*>).propertyValueTable = tableBuilder.build()
        }

        // Call the completion
        for (stateBuilder in stateBuilders) {
            stateBuilder.whenCompleted.forEach { it() }
        }
    }

    companion object {

        private val NAME = DataQuery.of("Name")
        private val PROPERTIES = DataQuery.of("Properties")

        fun <T, S : State<S>> deserializeState(dataView: DataView, registry: CatalogTypeRegistry<T>):
                S where T : CatalogType, T : StateContainer<S> {
            val id = dataView.getString(NAME).get()
            val catalogType = registry.require(NamespacedKey.resolve(id))

            var state = catalogType.defaultState
            val properties = dataView.getView(PROPERTIES).orElse(null)
            if (properties != null) {
                for ((key, rawValue) in properties.getValues(false)) {
                    val stateProperty = state.getStatePropertyByName(key.toString()).orElse(null)
                    if (stateProperty != null) {
                        val value = stateProperty.parseValue(rawValue.toString()).orElse(null)
                        if (value != null) {
                            @Suppress("UNCHECKED_CAST")
                            stateProperty as StateProperty<Comparable<Comparable<*>>>
                            val newState = state.withStateProperty(stateProperty, value.uncheckedCast()).orElse(null)
                            if (newState != null) {
                                state = newState
                            }
                        }
                    }
                }
            }

            return state
        }
    }

    private fun buildDataContainer(baseKey: NamespacedKey, values: Map<StateProperty<*>, Comparable<*>>): DataContainer {
        val dataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
        dataContainer[NAME] = baseKey.toString()

        if (values.isEmpty())
            return dataContainer

        val propertiesView = dataContainer.createView(PROPERTIES)
        for ((property, comparable) in values) {
            propertiesView[DataQuery.of(property.getName())] = comparable
        }

        return dataContainer
    }

    private fun buildKey(baseKey: NamespacedKey, values: Map<StateProperty<*>, Comparable<*>>): NamespacedKey {
        if (values.isEmpty())
            return baseKey

        val builder = StringBuilder()
        builder.append(baseKey.value).append('[')

        val propertyValues = mutableListOf<String>()
        for ((property, comparable) in values) {
            val value = if (comparable is Enum) comparable.name else comparable.toString()
            propertyValues.add(property.getName() + '=' + value.toLowerCase())
        }

        builder.append(propertyValues.joinToString(separator = ","))
        builder.append(']')

        return namespacedKey(baseKey.namespace, builder.toString())
    }

    override fun getValidStates(): ImmutableList<S> = this.validStates

    override fun getDefaultState(): S = this.validStates[0]

    override fun getStateProperties(): Collection<StateProperty<*>> = this.keysToProperty.values

    override fun getStatePropertyByName(statePropertyId: String): Optional<StateProperty<*>>
            = this.defaultState.getStatePropertyByName(statePropertyId)
}
