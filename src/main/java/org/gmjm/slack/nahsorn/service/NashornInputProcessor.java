package org.gmjm.slack.nahsorn.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.gmjm.script.Script;
import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NashornInputProcessor<T,U>
{

	private static final Logger logger = LoggerFactory.getLogger(NashornInputProcessor.class);
	protected static final Logger inScriptLogger = LoggerFactory.getLogger("InScriptLogger");


	protected ScriptLoader scriptLoader;

	protected ScriptFactory scriptFactory;


	Map<String, Function<Object[], U>> functionMap = new HashMap<>();
	Map<String, Script> scriptCache = new HashMap<>();

	public String loadScript(String name, String body)
	{
		Script script = scriptFactory.load(name, body);
		try
		{
			Function<Object[], U> function = scriptLoader.load(script);
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



	public abstract U process(String scriptName, T t);

	public Set<String> getLoadedScripts(){
		return functionMap.keySet();
	}

	public Script getLoadedScript(String name) {
		return scriptCache.get(name);
	}

	protected Function<Object[],U> getFunction(String scriptName)
	{
		if(!functionMap.containsKey(scriptName)) {
			throw new RuntimeException("Script not found: " + scriptName);
		}

		return functionMap.get(scriptName);
	}

}
