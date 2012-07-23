package com.brindysoft.mygist.formatter.java;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.mygist.api.LanguageFormatter;

@Component(immediate = true)
public class JavaLanguageFormatter implements LanguageFormatter {

	private static final String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch",
			"char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
			"finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
			"native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronised", "this", "throw", "throws", "transient", "try", "void", "volatile",
			"while", "false", "true", "null" };

	private static final Set<String> KEYWORD_SET = new HashSet<String>(Arrays.asList(KEYWORDS));

	private static final Map<String, String> WHITESPACE_REPLACEMENTS = new HashMap<String, String>();
	static {
		WHITESPACE_REPLACEMENTS.put(" ", "&nbsp;");
		WHITESPACE_REPLACEMENTS.put("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	private static final Map<String, String> OTHER_REPLACEMENTS = new HashMap<String, String>();
	static {
		OTHER_REPLACEMENTS.put("&", "&amp;");
	}

	@Override
	public String getName() {
		return "Java";
	}

    /**
     * This is a comment inside the &nbsp; "comment" block.
     */
	@Override
	public String[] toHTMLLines(String content) {
		// content = content.replaceAll(" ", "&nbsp;");
		// content = content.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

		StringTokenizer strtok = new StringTokenizer(content, " \t\n\"\\&", true);
		
		StringBuffer formatted = new StringBuffer();
		
		boolean quoted = false;
		boolean javadoc = false;
		boolean skip = false;
		boolean eolComment = false;
		boolean blockComment = false;
		
		while (strtok.hasMoreTokens()) {
			String token = strtok.nextToken();

			token = token.replaceAll("&", "&amp;");
			token = token.replaceAll(" ", "&nbsp;");
			token = token.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");

			if (skip) {
				// do nothing
				skip = false;
			} else if (quoted) {
				if ("\"".equals(token)) {
					token = token + "</span>";
					quoted = false;
				} else if ("\\".equals(token)) {
					skip = true;
				}
			} else if (javadoc) {
				if ("*/".equals(token)) {
					token = token + "</span>";
					javadoc = false;
				}
			} else if (blockComment) {
				if ("*/".equals(token)) {
					token = token + "</span>";
					blockComment = false;
				}
			} else if (eolComment) {
				if ("\n".equals(token)) {
					token = "</span>" + token;
					eolComment = false;
				}
			} else if (token.startsWith("//")) {
				token = "<span class='code-eol-comment' style='color: green'>" + token;
				eolComment = true;
			} else if ("\"".equals(token)) {
				token = "<span class='code-quoted' style='color: blue; font-weight: bold;'>" + token;
				quoted = true;
			} else if (token.startsWith("/**")) {
				token = "<span class='code-javadoc' style='color: blue'>" + token;
				javadoc = true;
			} else if ("/*".equals(token)) {
				token = "<span class='code-block-comment' style='color: green'>" + token;
				blockComment = true;
			} else if (KEYWORD_SET.contains(token)) {
				token = "<span class='code-keyword' style='color: purple'>" + token + "</span>";
			}
/*
			System.out.print(token);
*/
			formatted.append(token);
		}

		return formatted.toString().split("\n");
	}

}
