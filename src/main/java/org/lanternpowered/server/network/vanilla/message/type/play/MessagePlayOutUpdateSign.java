/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.objects.LocalizedText;

public final class MessagePlayOutUpdateSign implements Message {

    private final Vector3i position;
    private final LocalizedText[] lines;

    /**
     * Creates the update sign message.
     * 
     * @param position the position
     * @param lines the lines
     */
    public MessagePlayOutUpdateSign(Vector3i position, LocalizedText[] lines) {
        this.position = checkNotNull(position, "position");
        checkNotNull(lines, "lines");
        checkArgument(lines.length == 4, "lines length must be 4");
        this.lines = lines;
    }

    /**
     * Gets the sign position of this message.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

    /**
     * Gets the lines.
     * 
     * @return the lines
     */
    public LocalizedText[] getLines() {
        return this.lines;
    }
}
