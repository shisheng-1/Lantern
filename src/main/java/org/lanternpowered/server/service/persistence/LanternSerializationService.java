package org.lanternpowered.server.service.persistence;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import org.lanternpowered.server.configuration.DataSerializableTypeSerializer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.service.persistence.DataBuilder;
import org.spongepowered.api.service.persistence.SerializationService;

import java.util.Map;

public class LanternSerializationService implements SerializationService {

    static {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(DataSerializable.class),
                new DataSerializableTypeSerializer());
    }

    private final Map<Class<?>, DataBuilder<?>> builders = Maps.newHashMap();
    private boolean registrationComplete = false;

    public void completeRegistration() {
        checkState(!this.registrationComplete);
        this.registrationComplete = true;
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder) {
        checkNotNull(clazz);
        checkNotNull(builder);
        checkState(!this.registrationComplete);
        if (!this.builders.containsKey(clazz)) {
            this.builders.put(clazz, builder);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataSerializable> Optional<DataBuilder<T>> getBuilder(Class<T> clazz) {
        checkNotNull(clazz);
        if (this.builders.containsKey(clazz)) {
            return Optional.of((DataBuilder<T>) this.builders.get(clazz));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView) {
        Optional<DataBuilder<T>> optional = this.getBuilder(clazz);
        if (optional.isPresent()) {
            return optional.get().build(dataView);
        } else {
            return Optional.absent();
        }
    }
}