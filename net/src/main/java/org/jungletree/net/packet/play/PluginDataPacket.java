package org.jungletree.net.packet.play;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.net.Packet;
import org.jungletree.net.exception.PluginDataException;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PluginDataPacket implements Packet {

    String channel;
    byte[] data;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeString(this.channel);

        var data = this.data;
        if (data.length > Short.MAX_VALUE) {
            throw new PluginDataException(String.format(
                    "Plugin data exceeds the maximum length of %d for channel \"%s\": actualLength=%d",
                    Short.MAX_VALUE,
                    this.channel,
                    data.length));
        }

        buf.writeVarInt(data.length);
        buf.writeBytes(data);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.channel = buf.readString();
        int length = buf.remaining();
        if (length > Short.MAX_VALUE) {
            throw new PluginDataException(String.format(
                    "Plugin data exceeds the maximum length of %d for channel \"%s\": actualLength=%d",
                    Short.MAX_VALUE,
                    channel,
                    length));
        }
        byte[] data = new byte[length];
        buf.readBytes(data);
        this.data = data;
    }
}
