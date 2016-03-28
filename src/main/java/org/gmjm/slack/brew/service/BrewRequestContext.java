package org.gmjm.slack.brew.service;

import org.gmjm.slack.api.model.SlackCommand;
import org.gmjm.slack.brew.repositories.BrewRepository;

public class BrewRequestContext
{
	public final SlackCommand slackCommand;
	public final BrewRepository brewRepository;
	public final BrewCommand brewCommand;
	public final String user;


	public BrewRequestContext(SlackCommand slackCommand, BrewRepository brewRepository, BrewCommand brewCommand, String user)
	{
		this.slackCommand = slackCommand;
		this.brewRepository = brewRepository;
		this.brewCommand = brewCommand;
		this.user = user;
	}
}
