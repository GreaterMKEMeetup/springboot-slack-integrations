package org.gmjm.slack.brew.service;

import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.hook.HookResponse;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.model.SlackCommand;
import org.gmjm.slack.brew.repositories.BrewRepository;
import org.gmjm.slack.command.processor.SlackCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.gmjm.slack.api.hook.HookResponse.Status;

@Service("brewService")
public class BrewService implements SlackCommandProcessor
{
	private static final String COFFEE_BOT_USERNAME = "coffee-master";
	private static final String COFFEE_EMOJI = "coffee";
	private static final String COFFEE_CHANNEL = "coffee";

	private static final Logger logger = LoggerFactory.getLogger(BrewService.class);

	@Autowired
	private HookRequest hookRequest;

	@Autowired
	private HookRequestFactory hookRequestFactory;

	@Autowired
	BrewCommandHandlers brewCommandHandlers;

	@Autowired
	BrewRepository brewRepository;

	@Override
	public void process(SlackCommand slackCommand) {
		BrewCommand brewCommand = new BrewCommand(slackCommand.getText());
		BrewRequestContext brc = new BrewRequestContext(slackCommand,brewRepository,brewCommand,slackCommand.getUserName());

		sendPrivate(brc);
		sendPublic(brc);
	}

	private void sendPrivate(BrewRequestContext brc) {

		try {
			String handlerName = brc.brewCommand.command + "_ephemeral";

			SlackMessageBuilder builder = brewCommandHandlers.handle(handlerName,brc);

			if(builder == null) return;

			builder.setIconEmoji(COFFEE_EMOJI);
			builder.setResponseType("ephemeral");
			builder.setUsername(COFFEE_BOT_USERNAME);

			HookRequest responseHook = hookRequestFactory.createHookRequest(brc.slackCommand.getResponseUrl());

			HookResponse hookResponse = responseHook.send(builder.build());

			if(Status.FAILED.equals(hookResponse.getStatus())) {
				logger.error("Failed to send response: " + hookResponse.getMessage());
			}

		} catch (Exception e) {
			logger.error("Failed to send hookRequest.",e);
		}
	}

	private void sendPublic(BrewRequestContext brc) {
		try {
			String handlerName = brc.brewCommand.command + "_public";

			SlackMessageBuilder builder = brewCommandHandlers.handle(handlerName,brc);

			if(builder == null) return;

			builder.setIconEmoji(COFFEE_EMOJI);
			builder.setChannel(COFFEE_CHANNEL);
			builder.setUsername(COFFEE_BOT_USERNAME);

			hookRequest.send(builder.build());
		} catch (Exception e) {
			logger.error("Failed to send hookRequest.",e);
		}
	}
}
