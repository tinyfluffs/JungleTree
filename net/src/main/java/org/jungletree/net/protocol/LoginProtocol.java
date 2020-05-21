package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jungletree.net.http.HttpClient;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoginProtocol extends Protocol {

    @Getter HttpClient httpClient;

    public LoginProtocol(HttpClient httpClient) {
        super("LOGIN", 5);
        this.httpClient = httpClient;
    }
}
