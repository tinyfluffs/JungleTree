package org.jungletree.net.packet.status;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusRequestPacket implements Packet {

    @Override
    public void encode(ByteBuf buf) {}

    @Override
    public void decode(ByteBuf buf) {}
}
