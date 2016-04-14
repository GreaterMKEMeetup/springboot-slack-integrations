package org.gmjm.slack.core.message;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class StringTest
{

	@Test
	public void test() {

		List<String> strings = Arrays.asList("one","two","three");

		assertTrue(strings.contains(new String("one")));

	}

}
