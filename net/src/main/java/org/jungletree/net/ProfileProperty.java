package org.jungletree.net;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfileProperty {
    String name;
    String value;
    String signature;
}
