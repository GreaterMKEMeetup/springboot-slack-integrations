package org.gmjm.slack.nahsorn.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gmjm.nashorn.SimpleScript;
import org.gmjm.script.Script;
import org.gmjm.script.ScriptFactory;
import org.gmjm.script.ScriptLoader;
import org.gmjm.slack.api.model.SlackCommand;
import org.gmjm.slack.core.model.SlackCommandMapImpl;
import org.gmjm.slack.nahsorn.service.NashornCommandProcessor;
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

@Controller
public class NashornController
{

	private static Logger logger = LoggerFactory.getLogger(NashornController.class);

	@Value("#{'${slack.brew.valid_command_tokens}'.split(',')}")
	private List<String> tokens;

	@Value("${slack.nashorn.upload_token}")
	private String uploadToken;

	@Autowired
	private NashornCommandProcessor nashornCommandProcessor;


	@RequestMapping(method = RequestMethod.POST, value = "/js")
	public ResponseEntity<String> eval(
		@RequestParam String uploadToken,
		@RequestParam String name,
		@RequestBody String js
		)
	{

		if(!this.uploadToken.equals(uploadToken)) {
			return new ResponseEntity<String>("Invalid upload token.",HttpStatus.FORBIDDEN);
		}

		String newScript = nashornCommandProcessor.loadScript(name, js);

		return new ResponseEntity<>(newScript, HttpStatus.OK);
	}


	@RequestMapping(method = RequestMethod.POST, value = "/script")
	ResponseEntity<String> script(
		@RequestParam Map<String, String> params
	)
	{
		logger.info(params.toString());

		SlackCommand sc = new SlackCommandMapImpl(params);

		if(!tokens.contains(sc.getToken())) {
			return new ResponseEntity<String>("Invalid token.",HttpStatus.FORBIDDEN);
		}


		String[] commands = sc.getText().split(" ");

		if(commands.length > 0) {

			if(commands[0].equals("list")) {
				return new ResponseEntity<String>(getLoaded().toString(),HttpStatus.OK);
			}

			logger.info("attempting to run: {" + commands[0] + "}:{" + sc.getText()+"}");

			try
			{
				nashornCommandProcessor.process(commands[0], sc);
			} catch (Exception e) {
				logger.error("Failed",e);
				return new ResponseEntity<String>("Failed: " + e.getMessage(),HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<String>("No script specified.",HttpStatus.OK);
		}


		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/script/loaded")
	public @ResponseBody
	Set<String> getLoaded() {
		return nashornCommandProcessor.getLoadedScripts();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/script/loaded/{name}", produces = "application/javascript")
	public @ResponseBody
	String getLoadedBody(@PathVariable String name) {
		Script script =  nashornCommandProcessor.getLoadedScript(name);
		return script != null ? script.getBody() : "Does Not Exist";
	}

}
