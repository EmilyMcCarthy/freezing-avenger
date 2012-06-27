package com.cloudmine.coderunner.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.cloudmine.coderunner.SnippetContainer;
import com.cloudmine.coderunner.SnippetResponseConfiguration;

public class SampleSnippetContainer implements SnippetContainer {

	@Override
	public String getSnippetName() {
		return "fooSnippet";
	}

	@Override
	public Object runSnippet(SnippetResponseConfiguration responseConfiguration, Map<String, String[]> params) {
		Map<String, String> retVal = new HashMap<String, String>(1);
		retVal.put("msg", "Hello world, from a snippet!");
		
		for (Entry<String, String[]> entry : params.entrySet()) {
			retVal.put(entry.getKey(), entry.getValue()[0]);
		}
		return retVal;
	}

}
