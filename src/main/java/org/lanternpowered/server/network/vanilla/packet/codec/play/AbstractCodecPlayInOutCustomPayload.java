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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.UnknownPacket;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutUnregisterChannels;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public abstract class AbstractCodecPlayInOutCustomPayload implements Codec<Packet> {

    private static final AttributeKey<MultiPartMessage> FML_MULTI_PART_MESSAGE = AttributeKey.valueOf("fml-mpm");

    @Override
    public ByteBuffer encode(CodecContext context, Packet packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final String channel;
        final ByteBuffer content;
        if (packet instanceof ChannelPayloadPacket) {
            final ChannelPayloadPacket channelPayload = (ChannelPayloadPacket) packet;
            content = channelPayload.getContent();
            // Retain because content will be released when written, MessagePlayInOutChannelPayload
            // is ReferenceCounted so it will be cleaned up later.
            content.retain();
            channel = channelPayload.getChannel();
        } else if (packet instanceof PacketPlayInOutRegisterChannels) {
            content = encodeChannels(((PacketPlayInOutRegisterChannels) packet).getChannels());
            channel = "minecraft:register";
        } else if (packet instanceof PacketPlayInOutUnregisterChannels) {
            content = encodeChannels(((PacketPlayInOutUnregisterChannels) packet).getChannels());
            channel = "minecraft:unregister";
        } else {
            final MessageResult result = encode0(context, packet);
            channel = result.channel;
            content = result.byteBuf;
        }
        try {
            buf.writeString(channel);
            buf.writeBytes(content);
        } finally {
            content.release();
        }
        return buf;
    }

    @Override
    public Packet decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final String channel = buf.readLimitedString(LanternChannelRegistrar.MAX_NAME_LENGTH);
        final int length = buf.available();
        if (length > Short.MAX_VALUE) {
            throw new DecoderException("CustomPayload messages may not be longer then " + Short.MAX_VALUE + " bytes");
        }
        final ByteBuffer content = buf.slice();
        final Packet packet = decode0(context, content, channel);
        if (content.available() > 0) {
            Lantern.getLogger().warn("Trailing bytes {}b after decoding with custom payload message codec {} with channel {}!\n{}",
                    content.available(), getClass().getName(), channel, packet);
        }
        // Skip all the bytes, we already processed them
        buf.readerIndex(buf.readerIndex() + buf.available());
        return packet;
    }

    private Packet decode0(CodecContext context, ByteBuffer content, String channel) {
        if ("minecraft:register".equals(channel)) {
            final Set<String> channels = decodeChannels(content);
            channels.removeIf(c -> c.startsWith("FML"));
            if (!channels.isEmpty()) {
                return new PacketPlayInOutRegisterChannels(channels);
            }
        } else if ("minecraft:unregister".equals(channel)) {
            final Set<String> channels = decodeChannels(content);
            channels.removeIf(c -> c.startsWith("FML"));
            if (!channels.isEmpty()) {
                return new PacketPlayInOutUnregisterChannels(channels);
            }
        } else if ("FML|MP".equals(channel)) {
            Attribute<MultiPartMessage> attribute = context.getChannel().attr(FML_MULTI_PART_MESSAGE);
            MultiPartMessage message0 = attribute.get();
            if (message0 == null) {
                final String channel0 = content.readString();
                final int parts = content.readByte() & 0xff;
                final int size = content.readInt();
                if (size <= 0) {
                    throw new CodecException("Received FML MultiPart packet outside of valid length bounds, Received: " + size);
                }
                attribute.set(new MultiPartMessage(channel0, context.byteBufAlloc().buffer(size), parts));
            } else {
                int part = content.readByte() & 0xff;
                if (part != message0.index) {
                    throw new CodecException("Received FML MultiPart packet out of order, Expected: " + message0.index + ", Got: " + part);
                }
                int len = content.available() - 1;
                content.readBytes(message0.buffer, message0.offset, len);
                message0.offset += len;
                message0.index++;
                if (message0.index >= message0.parts) {
                    final Packet packet = decode0(context, message0.channel, message0.buffer);
                    attribute.set(null);
                    return packet;
                }
            }
        } else {
            return decode0(context, channel, content);
        }
        return UnknownPacket.INSTANCE;
    }

    protected abstract MessageResult encode0(CodecContext context, Packet packet) throws CodecException;

    protected abstract Packet decode0(CodecContext context, String channel, ByteBuffer content) throws CodecException;

    /**
     * Decodes the byte buffer into a set of channels.
     *
     * @param buffer the byte buffer
     * @return the channels
     */
    private static Set<String> decodeChannels(ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.available()];
        buffer.readBytes(bytes);
        return Sets.newHashSet(Splitter.on('\u0000').split(new String(bytes, StandardCharsets.UTF_8)));
    }

    /**
     * Encodes the set of channels into a byte buffer.
     *
     * @param channels the channels
     * @return the byte buffer
     */
    private static ByteBuffer encodeChannels(Set<String> channels) {
        return ByteBufferAllocator.unpooled().wrappedBuffer(Joiner.on('\u0000').join(channels).getBytes(StandardCharsets.UTF_8));
    }

    protected static class MessageResult {

        private final String channel;
        private final ByteBuffer byteBuf;

        public MessageResult(String channel, ByteBuffer byteBuf) {
            this.byteBuf = byteBuf;
            this.channel = channel;
        }
    }

    private static class MultiPartMessage {

        private final String channel;
        private final ByteBuffer buffer;
        private final int parts;

        private int index;
        private int offset;

        MultiPartMessage(String channel, ByteBuffer buffer, int parts) {
            this.channel = channel;
            this.buffer = buffer;
            this.parts = parts;
        }
    }

}