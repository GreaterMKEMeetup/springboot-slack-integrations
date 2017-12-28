package org.gmjm.slack.trivia;

import lombok.Data;

@Data
public class Event {

    private String token;
    private String challenge;
    private EventType type;

    boolean isVerifiedEvent(String slackToken) {
        return token.equals(slackToken);
    }

    enum EventType {
        url_verification
    }

}