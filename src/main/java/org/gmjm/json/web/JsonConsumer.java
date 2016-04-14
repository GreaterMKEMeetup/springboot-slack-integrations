package org.gmjm.json.web;

import org.springframework.http.ResponseEntity;

public interface JsonConsumer<T>
{

	ResponseEntity<T> consume(String consumerName, String json);

}
