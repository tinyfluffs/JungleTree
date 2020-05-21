package org.jungletree.api.chat;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;

@Data
@Builder
public class ChatMessage {
    String text;
    
    public final JSONObject toJson() {
        var result = new JSONObject();
        result.put("text", text);
        return result;
    }
}
