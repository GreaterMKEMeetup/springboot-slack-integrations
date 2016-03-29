package org.gmjm.slack.brew.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gmjm.slack.api.model.SlackCommand;
import org.gmjm.slack.command.processor.SlackCommandProcessor;
import org.gmjm.slack.core.model.SlackCommandMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class BrewController
{

	@Value("#{'${slack.brew.valid_command_tokens}'.split(',')}")
	private List<String> tokens;

	private static final Logger logger = LoggerFactory.getLogger(BrewController.class);

	@Autowired
	@Qualifier("brewService")
	private SlackCommandProcessor brewService;

	@RequestMapping(method = RequestMethod.POST, value = "/coffee")
	ResponseEntity<String> coffee(
		@RequestParam Map<String, String> params
	)
	{
		SlackCommand slackCommand = new SlackCommandMapImpl(params);

		if(!tokens.contains(slackCommand.getToken())) {
			return new ResponseEntity<String>("Invalid token.",HttpStatus.FORBIDDEN);
		}

		brewService.process(slackCommand);

		return ok(null);
	}

	private static ResponseEntity<String> ok(String string)
	{
		return new ResponseEntity<>(string, HttpStatus.OK);
	}

}
