package org.gmjm.slack.core.message;

import org.gmjm.slack.api.hook.HookRequest;
import org.gmjm.slack.api.hook.HookRequestFactory;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.gmjm.slack.core.hook.HttpsHookRequestFactory;
import org.junit.Test;

/**
 *
 * Use this to test sending messages to your incoming web hook without deploying.
 *
 */
public class IncomingWebHookTest
{

	private HookRequestFactory hookRequestFactory = new HttpsHookRequestFactory();
	private String testHookUrl = "https://localhost:8080/webhook/sink";
	private HookRequest testRequest = hookRequestFactory.createHookRequest(testHookUrl);

	private SlackMessageFactory messageFactory = new JsonMessageFactory();

	@Test
	public void testWebHook() {

		SlackMessageBuilder smb = messageFactory.createMessageBuilder();

		smb.setChannel("coffee");
		smb.setUserAsChannel("algassman");
		smb.setIconEmoji("coffee");
		smb.setText("Junit test of webhook.");

		//testRequest.send(smb.build());

	}

}
