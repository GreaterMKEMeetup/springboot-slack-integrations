package org.gmjm.slack.gitlab.web;


import java.io.IOException;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookResponse;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.core.message.ValidChannelName;
import org.gmjm.slack.gitlab.model.GitlabRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitlabController
{
	private static final String USER_EMOJI = "unicorn_face";
	private static final String USER_NAME = "Giticorn";

	private static final Logger logger = LoggerFactory.getLogger(GitlabController.class);

	@Autowired
	private Function<GitlabRequest,SlackMessageBuilder> transform;

	@Autowired
	private HookRequest hookRequest;

	@RequestMapping(method = RequestMethod.POST, value = "/git")
	ResponseEntity<String> coffee(HttpServletRequest request
	)
	{
		GitlabRequest gitlabRequest;
		try {
			gitlabRequest = new GitlabRequest(request.getInputStream());
			logger.info(gitlabRequest.getOjectKind() + " : " + gitlabRequest.getUserName() + " : " + gitlabRequest.getRepositoryName());

			if(!gitlabRequest.getOjectKind().equalsIgnoreCase("push"))
				return new ResponseEntity<>("Unsupported op: " + gitlabRequest.getOjectKind(), HttpStatus.OK);

		} catch (IOException e) {
			logger.error("Failed to create GitlabRequest.",e);
			return new ResponseEntity<>("Failed to create GitlabRequest.",HttpStatus.EXPECTATION_FAILED);
		}

		SlackMessageBuilder sm = transform.apply(gitlabRequest);

		sm.setIconEmoji(USER_EMOJI);
		sm.setUsername(USER_NAME);

		try	{
			//global commit channel
			sm.setChannel(new ValidChannelName("gitlab-commits").getValue());
			HookResponse hookResponse = hookRequest.send(sm.build());
			logger.info("Global push response: " + hookResponse.getMessage());
		} catch (Exception e) {
			logger.error("Failed to send push message to slack default channel.");
		}

		String validChannelName = new ValidChannelName(gitlabRequest.getRepositoryName()).getValue();

		try {
			sm.setChannel(validChannelName);
			//repo specific channel
			HookResponse hookResponse = hookRequest.send(sm.build());
			logger.info(String.format("%s push response: %s",validChannelName,hookResponse.getMessage()));
		} catch (Exception e) {
			logger.error("Failed to send push message to repository channel: " + validChannelName);
		}

		return new ResponseEntity<>("Success",HttpStatus.OK);
	}

}
