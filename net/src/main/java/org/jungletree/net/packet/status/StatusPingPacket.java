package org.jungletree.net.packet.status;

import org.jungletree.net.Packet;

import java.util.Objects;
import java.util.StringJoiner;

public class StatusPingPacket implements Packet {

    private final long time;

    public StatusPingPacket(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusPingPacket that = (StatusPingPacket) o;
        return time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatusPingPacket.class.getSimpleName() + "[", "]")
                .add("time=" + time)
                .toString();
    }
}
