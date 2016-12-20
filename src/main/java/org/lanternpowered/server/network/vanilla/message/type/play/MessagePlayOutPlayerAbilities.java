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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayOutPlayerAbilities implements Message {

    private final boolean flying;
    private final boolean canFly;
    private final float flySpeed;
    private final float fieldOfView;
    private final boolean invulnerable;
    private final boolean creative;

    public MessagePlayOutPlayerAbilities(boolean flying, boolean canFly, boolean invulnerable,
            boolean creative, float flySpeed, float fieldOfView) {
        this.fieldOfView = fieldOfView;
        this.flySpeed = flySpeed;
        this.flying = flying;
        this.canFly = canFly;
        this.invulnerable = invulnerable;
        this.creative = creative;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public float getFieldOfView() {
        return this.fieldOfView;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isCreative() {
        return this.creative;
    }
}
