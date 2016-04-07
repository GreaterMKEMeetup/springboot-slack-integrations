package org.gmjm.slack.nahsorn;

import java.io.IOException;

import org.gmjm.nashorn.ContextWrapper;
import org.gmjm.nashorn.DynamicScriptLoader;
import org.gmjm.nashorn.ScriptFactoryImpl;
import org.gmjm.script.Script;
import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class NashornConfig
{

	ScriptLoader scriptLoader = new DynamicScriptLoader();

	@Bean
	public ScriptLoader scriptLoader() {

		return scriptLoader;
	}

	@Bean
	public ScriptFactory scriptFactory(ResourceLoader resourceLoader) throws IOException
	{

		ScriptFactory base = new ScriptFactoryImpl();

		Script slackProcessorPreScript = base.load("slackProcessorPreScript.js",resourceLoader.getResource("classpath:nashorn/slackProcessorPreScript.js").getURI());
		Script slackProcessorPostScript = base.load("slackProcessorPreScript.js",resourceLoader.getResource("classpath:nashorn/slackProcessorPostScript.js").getURI());

		ContextWrapper contextWrapper = new ContextWrapper(
			slackProcessorPreScript,
			slackProcessorPostScript,
			"slackCommand",
			"hookRequest",
			"hookRequestFactory",
			"slackMessageFactory",
			"inScriptLogger");

		ScriptFactory scriptFactory = new ScriptFactoryImpl(contextWrapper);

		return scriptFactory;
	}

}
