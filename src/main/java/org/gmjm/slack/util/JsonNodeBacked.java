package org.gmjm.slack.util;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeBacked
{

	protected JsonNode jsonNode;

	public JsonNodeBacked(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	protected String get(String ... keys) {
		return get(jsonNode, Arrays.asList(keys));
	}

	protected String get(JsonNode node, List<String> keys) {
		if(keys.isEmpty()) {
			return node.asText();
		}

		return get(node.findValue(keys.get(0)),keys.subList(1,keys.size()));
	}

}
