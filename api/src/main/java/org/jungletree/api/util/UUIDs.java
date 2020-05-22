package org.jungletree.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDs {

    public static UUID fromFlatString(String str) {
        return UUID.fromString(str.substring(0, 8)
                + "-" + str.substring(8, 12)
                + "-" + str.substring(12, 16)
                + "-" + str.substring(16, 20)
                + "-" + str.substring(20, 32));
    }
}
