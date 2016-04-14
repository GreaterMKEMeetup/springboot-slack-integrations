package org.gmjm.json.nashorn;

import org.gmjm.json.web.JsonConsumer;
import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.gmjm.slack.nahsorn.service.NashornInputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NashornJsonConsumer extends NashornInputProcessor<String,String> implements JsonConsumer<String>
{

	@Autowired
	@Qualifier("jsonConsumerScriptLoader")
	public void setScriptLoader(ScriptLoader scriptLoader) {
		this.scriptLoader = scriptLoader;
	}

	@Autowired

	@Qualifier("jsonConsumerScriptFactory")
	public void setScriptFactory(ScriptFactory scriptFactory) {
		this.scriptFactory = scriptFactory;
	}

	private static final Logger logger = LoggerFactory.getLogger(NashornJsonConsumer.class);


	@Override
	public ResponseEntity<String> consume(String consumerName, String jsonInput)
	{
		try
		{
			String result = process(consumerName, jsonInput);
			return new ResponseEntity<String>(result, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Failed to process jsonInput with consumer: " + consumerName;
			logger.error(message, e);
			return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public String process(String scriptName, String jsonInput)
	{
		Object o = getFunction(scriptName)
			.apply(new Object[]{inScriptLogger,jsonInput});

		return o.toString();
	}
}
