package com.brindysoft.mygist.formatter.text;

import java.util.LinkedList;
import java.util.List;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mygist.api.LanguageFormatter;

@Component(immediate = true)
public class PlainTextLanguageFormatter implements LanguageFormatter {

	@Override
	public String getName() {
		return "Plain Text";
	}

	@Override
	public String[] toHTMLLines(String content) {
		String[] lines = content.split("\n");

		List<String> lineList = new LinkedList<String>();
		for (String line : lines) {
			line = line.replace(" ", "&nbsp;");
			line = line.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			lineList.add(line);
		}

		return lineList.toArray(new String[lineList.size()]);
	}

}
