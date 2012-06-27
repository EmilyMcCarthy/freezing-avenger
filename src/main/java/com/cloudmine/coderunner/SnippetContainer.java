package com.cloudmine.coderunner;

import java.io.IOException;
import java.util.Map;

public interface SnippetContainer {
	public String getSnippetName();
	public String runSnippet(Map<String, String> params) throws IOException;
}
