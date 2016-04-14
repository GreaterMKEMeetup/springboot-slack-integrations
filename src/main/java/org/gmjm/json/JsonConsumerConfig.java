package org.gmjm.json;

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
public class JsonConsumerConfig
{

	ScriptLoader scriptLoader = new DynamicScriptLoader();

	@Bean(name="jsonConsumerScriptLoader")
	public ScriptLoader scriptLoader() {

		return scriptLoader;
	}

	@Bean(name="jsonConsumerScriptFactory")
	public ScriptFactory scriptFactory(ResourceLoader resourceLoader) throws IOException
	{

		ScriptFactory base = new ScriptFactoryImpl();

		Script jsonProcessorPreScript = base.load("jsonProcessorPreScript.js",resourceLoader.getResource("classpath:json/jsonProcessorPreScript.js").getURI());
		Script jsonProcessorPostScript = base.load("jsonProcessorPostScript.js",resourceLoader.getResource("classpath:json/jsonProcessorPostScript.js").getURI());

		ContextWrapper contextWrapper = new ContextWrapper(
			jsonProcessorPreScript,
			jsonProcessorPostScript,
			"logger",
			"jsonString");

		ScriptFactory scriptFactory = new ScriptFactoryImpl(contextWrapper);

		return scriptFactory;
	}

}
