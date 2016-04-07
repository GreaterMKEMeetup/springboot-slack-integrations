package org.gmjm.slack.nahsorn.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.gmjm.script.Script;
import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.api.model.SlackCommand;
import org.gmjm.slack.command.processor.SlackCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NashornCommandProcessor
{

	private static final Logger logger = LoggerFactory.getLogger(NashornCommandProcessor.class);
	private static final Logger inScriptLogger = LoggerFactory.getLogger("InScriptLogger");

	@Autowired
	private ScriptLoader scriptLoader;

	@Autowired
	private ScriptFactory scriptFactory;

	@Autowired
	private HookRequest hookRequest;

	@Autowired
	private HookRequestFactory hookRequestFactory;

	@Autowired
	private SlackMessageFactory slackMessageFactory;

	Map<String, Function<Object[], SlackMessageBuilder>> functionMap = new HashMap<>();
	Map<String, Script> scriptCache = new HashMap<>();

	public String loadScript(String name, String body)
	{
		Script script = scriptFactory.load(name, body);
		try
		{
			Function<Object[], SlackMessageBuilder> function = scriptLoader.load(script);
			functionMap.put(name, function);

			scriptCache.put(name,script);

			return script.getBody();
		}
		catch (Exception e)
		{
			logger.error("Failed to parse script: ", e);
			return e.getMessage();
		}
	}



	public void process(String scriptName, SlackCommand slackCommand)
	{
		if(!functionMap.containsKey(scriptName)) {
			throw new RuntimeException("Script not found: " + scriptName);
		}

		functionMap.get(scriptName)
			.apply(new Object[]{
				slackCommand,
				hookRequest,
				hookRequestFactory,
				slackMessageFactory,
				inScriptLogger});
	}

	public Set<String> getLoadedScripts(){
		return functionMap.keySet();
	}

	public Script getLoadedScript(String name) {
		return scriptCache.get(name);
	}

}
