package org.gmjm.slack.context;

import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.api.model.SlackCommand;

public class SlackContext
{
	private final SlackCommand slackCommand;
	private final HookRequest hookRequest;
	private final HookRequestFactory hookRequestFactory;
	private final SlackMessageFactory slackMessageFactory;


	public SlackContext(SlackCommand slackCommand, HookRequest hookRequest, HookRequestFactory hookRequestFactory, SlackMessageFactory slackMessageFactory)
	{
		this.slackCommand = slackCommand;
		this.hookRequest = hookRequest;
		this.hookRequestFactory = hookRequestFactory;
		this.slackMessageFactory = slackMessageFactory;
	}


	public SlackCommand getSlackCommand()
	{
		return slackCommand;
	}


	public HookRequest getHookRequest()
	{
		return hookRequest;
	}


	public HookRequestFactory getHookRequestFactory()
	{
		return hookRequestFactory;
	}


	public SlackMessageFactory getSlackMessageFactory()
	{
		return slackMessageFactory;
	}
}
