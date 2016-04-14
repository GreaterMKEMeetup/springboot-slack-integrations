package org.gmjm.slack.nahsorn.service;

import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.api.model.SlackCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NashornSlackCommandProcessor extends NashornInputProcessor<SlackCommand,SlackMessageBuilder>
{

	@Autowired
	@Qualifier("nashornScriptLoader")
	public void setScriptLoader(ScriptLoader scriptLoader) {
		this.scriptLoader = scriptLoader;
	}

	@Autowired

	@Qualifier("nashornScriptFactory")
	public void setScriptFactory(ScriptFactory scriptFactory) {
		this.scriptFactory = scriptFactory;
	}

	@Autowired
	private HookRequest hookRequest;

	@Autowired
	private HookRequestFactory hookRequestFactory;

	@Autowired
	private SlackMessageFactory slackMessageFactory;


	@Override
	public SlackMessageBuilder process(String scriptName, SlackCommand slackCommand)
	{
		return getFunction(scriptName)
			.apply(new Object[]{
				slackCommand,
				hookRequest,
				hookRequestFactory,
				slackMessageFactory,
				inScriptLogger});
	}
}
