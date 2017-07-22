/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inject;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.DependencyAndSource;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProvisionListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Allows injecting the {@link InjectionPoint} in {@link Provider}s.
 *
 * TODO: The {@link ProvisionInvocation#getDependencyChain()} is deprecated in
 * TODO:   and will be removed in the Guice version 4.4. A proper replacement
 * TODO:   should be found at that point.
 */
public final class InjectionPointProvider extends AbstractMatcher<Binding<?>> implements Module, ProvisionListener, Provider<InjectionPoint> {

    @Nullable private InjectionPoint injectionPoint;

    @Nullable
    @Override
    public InjectionPoint get() {
        return this.injectionPoint;
    }

    @Override
    public boolean matches(Binding<?> binding) {
        return binding instanceof ProviderInstanceBinding && ((ProviderInstanceBinding) binding).getUserSuppliedProvider() == this;
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        try {
            this.injectionPoint = findInjectionPoint(provision.getDependencyChain());
            provision.provision();
        } finally {
            this.injectionPoint = null;
        }
    }

    @Nullable
    private static InjectionPoint findInjectionPoint(List<DependencyAndSource> dependencyChain) {
        if (dependencyChain.size() < 3) {
            new AssertionError("Provider is not included in the dependency chain").printStackTrace();
        }

        // @Inject InjectionPoint is the last, so we can skip it
        for (int i = dependencyChain.size() - 2; i >= 0; i--) {
            final Dependency<?> dependency = dependencyChain.get(i).getDependency();
            if (dependency == null) {
                return null;
            }
            final com.google.inject.spi.InjectionPoint spiInjectionPoint = dependency.getInjectionPoint();
            if (spiInjectionPoint != null) {
                final TypeToken<?> source = TypeToken.of(spiInjectionPoint.getDeclaringType().getType());
                final Member member = spiInjectionPoint.getMember();
                final InjectionPoint injectionPoint;
                if (member instanceof Field) {
                    final Field field = (Field) member;
                    injectionPoint = new InjectionPoint(source, TypeToken.of(field.getGenericType()), field.getAnnotations());
                } else if (member instanceof Executable) {
                    final Executable executable = (Executable) member;
                    final Annotation[][] parameterAnnotations = executable.getParameterAnnotations();
                    final Type[] parameterTypes = executable.getGenericParameterTypes();
                    final int index = dependency.getParameterIndex();
                    injectionPoint = new InjectionPoint(source, TypeToken.of(parameterTypes[index]), parameterAnnotations[index]);
                } else {
                    throw new IllegalStateException("Unsupported Member type: " + member.getClass().getName());
                }
                return injectionPoint;
            }
        }

        return null;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(InjectionPoint.class).toProvider(this);
        binder.bindListener(this, this);
    }
}
