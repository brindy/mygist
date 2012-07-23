package com.brindysoft.mygist.api.model;

public class GistFile {

	private String fileName;

	private String content;

	private String languageType;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setLanguageType(String languageType) {
		this.languageType = languageType;
	}

	public String getLanguageType() {
		return languageType;
	}

}
