package org.gmjm.json.web;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.gmjm.json.nashorn.NashornJsonConsumer;
import org.gmjm.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
public class JsonConsumingController
{

	private static final Logger logger = LoggerFactory.getLogger(JsonConsumingController.class);

	private static final List<String> RESERVED = Arrays.asList("load","loaded");

	@Value("${slack.nashorn.upload_token}")
	private String uploadToken;

	@Autowired
	private NashornJsonConsumer nashornJsonConsumer;


	@RequestMapping(value= "/json/{consumerName}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	ResponseEntity<String> consumeJson(
		@PathVariable("consumerName") String consumerName,
		HttpServletRequest request
	)
	{

		logger.info("Processing requeset for consumer: " + consumerName);

		try
		{
			InputStream is = request.getInputStream();
			String jsonString = IOUtils.toString(is);
			is.close();

			logger.info("Processing JSON message");

			return nashornJsonConsumer.consume(consumerName,jsonString);

		} catch (Exception e)
		{
			String message = "Failed to retrieve JSON string from request.getInputStream()";
			logger.error(message,e);
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/json/load")
	public ResponseEntity<String> eval(
		@RequestParam String uploadToken,
		@RequestParam String name,
		@RequestBody String js
	)
	{

		String scriptName = name.trim().toLowerCase();

		if(RESERVED.contains(scriptName)) {
			return new ResponseEntity<String>(String.format("{%s} is a reserved scriptName.",scriptName),HttpStatus.BAD_REQUEST);
		}

		if(!this.uploadToken.equals(uploadToken)) {
			return new ResponseEntity<String>("Invalid upload token.",HttpStatus.FORBIDDEN);
		}

		String newScript = nashornJsonConsumer.loadScript(name, js);

		return new ResponseEntity<>(newScript, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/json/loaded")
	public @ResponseBody
	Set<String> getLoaded() {
		return nashornJsonConsumer.getLoadedScripts();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/json/loaded/{name}", produces = "application/javascript")
	public @ResponseBody
	String getLoadedBody(@PathVariable String name) {
		Script script =  nashornJsonConsumer.getLoadedScript(name);
		return script != null ? script.getBody() : "Does Not Exist";
	}

}
