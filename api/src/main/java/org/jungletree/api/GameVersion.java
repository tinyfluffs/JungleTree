package org.jungletree.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.json.JSONObject;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum GameVersion {
    VERSION_1_16_5("1.16.5", 754);
    
    String name;
    int protocol;

    GameVersion(String name, int protocolVersion) {
        this.name = name;
        this.protocol = protocolVersion;
    }
    
    public JSONObject toJson() {
        var result = new JSONObject();
        result.put("name", this.name);
        result.put("protocol", this.protocol);
        return result;
    }
}
