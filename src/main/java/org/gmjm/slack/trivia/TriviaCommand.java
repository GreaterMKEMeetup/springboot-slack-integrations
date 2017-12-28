package org.gmjm.slack.trivia;

import lombok.Data;

@Data
public class TriviaCommand {

    private String token;
    private String command;
    private String text;

    public boolean isValidRequest(String slackToken) {
        return slackToken.equals(token);
    }

    public String response() {
        if (text.equals("start")) {
            return "Trivia Started!";
        } else if (text.equals("stop")) {
            return "Trivia Stopped!";
        }
        return "Unknown command: " + text;
    }

}