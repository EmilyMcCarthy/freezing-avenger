package com.cloudmine.coderunner.examples;

import java.util.Map;
import java.util.Map.Entry;

import com.cloudmine.coderunner.SnippetContainer;

public class SampleSnippetContainer implements SnippetContainer {

	@Override
	public String getSnippetName() {
		return "fooSnippet";
	}

	@Override
	public String runSnippet(Map<String, String[]> params) {
		String retVal = "Hello world, from a snippet!";
		for (Entry<String, String[]> entry : params.entrySet()) {
			retVal += "\nName: " + entry.getKey() + ", Value: " + entry.getValue()[0];
		}
		return retVal;
	}

}
