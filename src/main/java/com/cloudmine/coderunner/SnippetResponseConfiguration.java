package com.cloudmine.coderunner;

public class SnippetResponseConfiguration {
	private String mimeType;

	public SnippetResponseConfiguration() {
		mimeType = "application/json";
	}

	public SnippetResponseConfiguration mimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public String getMimeType() {
		return this.mimeType;
	}
}
