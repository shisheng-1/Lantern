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
package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.registry.Registries;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.BlockVolume;

public final class ExtentBufferHelper {

    public static short[] copyToArray(BiomeArea area, Vector2i min, Vector2i max, Vector2i size) {
        // Check if the area has more biomes than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the biomes to an array because the size limit was reached!");
        }
        final short[] copy = new short[(int) memory];
        int i = 0;
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                copy[i++] = Registries.getBiomeRegistry().getInternalId(area.getBiome(y, x));
            }
        }
        return copy;
    }

    public static BiomeType[] copyToObjectArray(BiomeArea area, Vector2i min, Vector2i max, Vector2i size) {
        // Check if the area has more biomes than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the biomes to an array because the size limit was reached!");
        }
        final BiomeType[] copy = new BiomeType[(int) memory];
        int i = 0;
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                copy[i++] = area.getBiome(y, x);
            }
        }
        return copy;
    }

    public static short[] copyToArray(BlockVolume volume, Vector3i min, Vector3i max, Vector3i size) {
        // Check if the volume has more blocks than can be stored in an array
        final long memory = (long) size.getX() * (long) size.getY() * (long) size.getZ();
        // Leave 8 bytes for a header used in some JVMs
        if (memory > Integer.MAX_VALUE - 8) {
            throw new OutOfMemoryError("Cannot copy the blocks to an array because the size limit was reached!");
        }
        final short[] copy = new short[(int) memory];
        int i = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    copy[i++] = Registries.getBlockRegistry().getStateInternalIdAndData(volume.getBlock(x, y, z));
                }
            }
        }
        return copy;
    }

    private ExtentBufferHelper() {
    }
}
