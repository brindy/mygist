package com.brindysoft.mygist.api;

public interface LanguageFormatter {

	String getName();

	String[] toHTMLLines(String content);

}
