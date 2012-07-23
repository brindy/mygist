package com.brindysoft.mygist.formatter.html;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mygist.api.LanguageFormatter;

@Component(immediate = true)
public class HtmlLanguageFormatter implements LanguageFormatter {

	@Override
	public String getName() {
		return "HTML";
	}

	public String[] toHTMLLines(String content) {
		return content.split("\n");
	}

}
