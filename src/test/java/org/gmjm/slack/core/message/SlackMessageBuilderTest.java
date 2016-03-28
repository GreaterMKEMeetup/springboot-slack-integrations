package org.gmjm.slack.core.message;

import org.junit.Test;

import org.gmjm.slack.core.message.AttachmentBuilderJsonImpl;
import org.gmjm.slack.core.message.SlackMessageBuilderJsonImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SlackMessageBuilderTest
{


	@Test
	public void testSlackMessageToJson() throws JsonProcessingException
	{


		SlackMessageBuilderJsonImpl sm = new SlackMessageBuilderJsonImpl();

		sm.setText("So many commits!");

		{
			AttachmentBuilderJsonImpl attachmentBuilder1 = new AttachmentBuilderJsonImpl();
			attachmentBuilder1.setTitle("Update Catalan translation to e38cb41", "http://example.com/mike/");
			attachmentBuilder1.setText("Modified *18* files");
			sm.addAttachment(attachmentBuilder1);
		}

		{
			AttachmentBuilderJsonImpl attachmentBuilder1 = new AttachmentBuilderJsonImpl();
			attachmentBuilder1.setTitle("mmk", "http://example.com/mike/");
			attachmentBuilder1.setText("Modified *13* files");
			sm.addAttachment(attachmentBuilder1);
		}

		{
			AttachmentBuilderJsonImpl attachmentBuilder1 = new AttachmentBuilderJsonImpl();
			attachmentBuilder1.setTitle("nowerk", "http://example.com/mike/");
			attachmentBuilder1.setText("Modified *5* files");
			sm.addAttachment(attachmentBuilder1);
		}


		System.out.println(sm.buildJsonString());


	}

}
