package org.gmjm.slack.gitlab.transform;

import java.util.List;
import java.util.function.Function;

import org.gmjm.slack.api.message.AttachmentBuilder;
import org.gmjm.slack.api.message.SlackMessageBuilder;
import org.gmjm.slack.api.message.SlackMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.gmjm.slack.gitlab.model.GitlabRequest;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class GitlabCommitToMessage implements Function<GitlabRequest,SlackMessageBuilder>
{
	@Autowired
	SlackMessageFactory slackMessageFactory;

	@Override
	public SlackMessageBuilder apply(GitlabRequest gitlabRequest)
	{
		List<GitlabRequest.Commit> commits = gitlabRequest.getCommits();

		SlackMessageBuilder slackMessageBuilder = slackMessageFactory.createMessageBuilder();
		slackMessageBuilder.setText(String.format(
			"%s pushed *%s* commits to <%s|%s>",
			gitlabRequest.getUserName(),
			commits.size(),
			gitlabRequest.getRepositoryHomepage(),
			gitlabRequest.getRepositoryName()));

		commits.stream()
			.map(commit -> {
				AttachmentBuilder attachmentBuilder = slackMessageFactory.createAttachmentBuilder();

				String commitMessage =
					StringUtils.isNullOrEmpty(commit.getMessage())
						? String.format("--no commit message, shame on you %s!", getAuthor(commit)) : commit.getMessage();

				attachmentBuilder.setTitle(commitMessage,commit.getUrl());
				attachmentBuilder.setText(String.format(
					"*(%s) +(%s) -(%s)",
					commit.getModified().size(),
					commit.getAdded().size(),
					commit.getRemoved().size()));
				return attachmentBuilder;
			})
			.forEach(slackMessageBuilder::addAttachment);

		return slackMessageBuilder;
	}

	private static String getAuthor(GitlabRequest.Commit commit) {
		GitlabRequest.Author author = commit.getAuthor();
		return author == null ? "" : author.getAuthorName();
	}
}
