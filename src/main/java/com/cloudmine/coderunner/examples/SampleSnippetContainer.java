package com.cloudmine.coderunner.examples;

import java.util.Map;

import com.cloudmine.coderunner.SnippetContainer;

public class SampleSnippetContainer implements SnippetContainer {

	@Override
	public String getSnippetName() {
		return "fooSnippet";
	}

	@Override
	public String runSnippet(Map<String, String> params) {
		return "Hello world, from a snippet!";
	}

}
