package org.gmjm.slack.context;

import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.api.model.SlackCommand;

public class SlackContextFactory
{

	private final HookRequest hookRequest;
	private final HookRequestFactory hookRequestFactory;
	private final SlackMessageFactory slackMessageFactory;


	public SlackContextFactory(HookRequest hookRequest, HookRequestFactory hookRequestFactory, SlackMessageFactory slackMessageFactory)
	{
		this.hookRequest = hookRequest;
		this.hookRequestFactory = hookRequestFactory;
		this.slackMessageFactory = slackMessageFactory;
	}

	public SlackContext create(SlackCommand slackCommand) {
		return new SlackContext(slackCommand,hookRequest,hookRequestFactory,slackMessageFactory);
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
