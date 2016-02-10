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
package org.lanternpowered.server.network.message.codec.serializer.defaults;

import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.codec.serializer.SerializerContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.message.codec.serializer.ValueSerializer;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.text.Text;

public final class SerializerLocalizedText implements ValueSerializer<LocalizedText> {

    @Override
    public void write(SerializerContext context, ByteBuf buf, LocalizedText object) throws CodecException {
        JsonTextTranslatableSerializer.setCurrentLocale(object.getLocale());
        context.write(buf, Types.STRING, SerializerText.GSON.toJson(object.getText()));
        JsonTextTranslatableSerializer.removeCurrentLocale();
    }

    @Override
    public LocalizedText read(SerializerContext context, ByteBuf buf) throws CodecException {
        try {
            return new LocalizedText(SerializerText.GSON.fromJson(context.read(buf, Types.STRING), Text.class),
                    JsonTextTranslatableSerializer.getCurrentLocale());
        } catch (JsonSyntaxException e) {
            throw new CodecException(e);
        }
    }

}
