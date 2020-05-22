package org.jungletree.api.player;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfileItem {
    String name;
    String value;
    String signature;
}
