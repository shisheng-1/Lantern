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
package org.lanternpowered.api.behavior

/**
 * An exception that is thrown when something unexpected
 * happened within a [Behavior].
 */
class BehaviorException : RuntimeException {

    /**
     * Constructs a new [BehaviorException].
     */
    constructor() : super()

    /**
     * Constructs a new [BehaviorException] with the
     * given message.
     *
     * @param message The message
     */
    constructor(message: String) : super(message)

    /**
     * Constructs a new [BehaviorException] with the
     * given message and underlying cause.
     *
     * @param message The message
     * @param cause The underlying cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)

    /**
     * Constructs a new [BehaviorException] with the
     * underlying cause.
     *
     * @param cause The underlying cause
     */
    constructor(cause: Throwable) : super(cause)

}