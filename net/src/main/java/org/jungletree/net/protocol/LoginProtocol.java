package org.jungletree.net.protocol;

import org.jungletree.net.http.HttpClient;

public class LoginProtocol extends Protocol {

    public LoginProtocol(HttpClient httpClient) {
        super("LOGIN", 5);
    }
}
