package org.gmjm.slack.trivia;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trivia")
@CommonsLog
public class TriviaEventsResource {

    @Value("${slack.token}")
    private String slackToken;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> handleCommand(@ModelAttribute TriviaCommand triviaCommand) {
        if (triviaCommand.isValidRequest(slackToken)) {
            return ResponseEntity.ok(triviaCommand.response());
        }
        return ResponseEntity.badRequest().body("invalid request");
    }

    @PostMapping(value="/events", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleEvent(@RequestBody Event event) {
        if (event.getType().equals(Event.EventType.url_verification) && event.isVerifiedEvent(slackToken)) {
            return event.getChallenge();
        }
        log.info("Received event: " + event);
        return "";
    }

}
