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
package org.lanternpowered.server.text.channel

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.Text
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.chat.ChatType
import java.util.Optional

/**
 * A message channel that targets all subjects contained within the given
 * channels and applies the message transformations of each channel in
 * order (so with n channels,
 * `channels[n-1].transformMessage(channels[n-2]
 * .transformMessage(channels[...]
 * .transformMessage(channels[0].transformMessage(input))))` would occur).
 */
class CombinedMessageChannel(channels: Iterable<MessageChannel>) : MessageChannel {

    private val channels = ImmutableSet.copyOf(channels)

    override fun transformMessage(sender: Any?, recipient: MessageReceiver, original: Text, type: ChatType): Optional<Text> {
        var text = original
        for (channel in this.channels) {
            text = channel.transformMessage(sender, recipient, text, type).orElse(text)
        }
        return text.optional()
    }

    override fun getMembers(): Collection<MessageReceiver> {
        return this.channels.stream()
                .flatMap { channel -> channel.members.stream() }
                .collect(ImmutableSet.toImmutableSet())
    }
}
