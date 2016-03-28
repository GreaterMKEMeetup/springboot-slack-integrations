package org.gmjm.slack.gitlab.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.gmjm.slack.util.JsonNodeBacked;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GitlabRequest extends JsonNodeBacked
{

	public static class Author extends JsonNodeBacked {
		public Author(JsonNode authorNode) {
			super(authorNode);
		}

		public String getAuthorName() {
			return get("name");
		}
	}

	public static class Commit extends JsonNodeBacked{

		public Commit(JsonNode commitsNode) {
			super(commitsNode);
		}

		public String getMessage() {
			return get("message");
		}

		public String getUrl() {
			return get("url");
		}

		public Author getAuthor() {
			return new Author(this.jsonNode.findValue("author"));
		}

		public List<String> getModified() {
			return childrenToStream(this.jsonNode.findValue("modified"))
				.map(jsonNode -> jsonNode.asText())
				.collect(Collectors.toList());
		}

		public List<String> getAdded() {
			return childrenToStream(this.jsonNode.findValue("added"))
				.map(jsonNode -> jsonNode.asText())
				.collect(Collectors.toList());
		}

		public List<String> getRemoved() {
			return childrenToStream(this.jsonNode.findValue("removed"))
				.map(jsonNode -> jsonNode.asText())
				.collect(Collectors.toList());
		}

	}

	private static final ObjectMapper om = new ObjectMapper();

	public GitlabRequest(InputStream inputStream) throws IOException
	{
		super(om.readTree(inputStream));
	}


	public String getOjectKind() {
		return get("object_kind");
	}

	public String getUserName() {
		return get("user_name");
	}

	public String getRepositoryName() {
		return get("repository","name");
	}

	public String getRepositoryHomepage() {
		return get("repository","homepage");
	}

	public List<Commit> getCommits() {
		return
			childrenToStream(this.jsonNode.findValue("commits"))
				.map(Commit::new)
				.collect(Collectors.toList());

	}

	private static Stream<JsonNode> childrenToStream(JsonNode jsonNode) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
			jsonNode.elements(),
			Spliterator.ORDERED
		),false);
	}

}
