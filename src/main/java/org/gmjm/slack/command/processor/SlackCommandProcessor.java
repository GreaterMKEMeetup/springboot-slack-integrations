package org.gmjm.slack.command.processor;

import org.gmjm.slack.api.model.SlackCommand;

public interface SlackCommandProcessor
{
	void process(SlackCommand slackCommand);
}
