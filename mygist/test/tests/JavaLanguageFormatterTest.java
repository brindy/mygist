package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.brindysoft.mygist.formatter.java.JavaLanguageFormatter;

public class JavaLanguageFormatterTest {

	@Test
	public void testQuotedKeywordNotFormatted() {
		String content = "public String hello = \"class\"";
		
		String[] lines = new JavaLanguageFormatter().toHTMLLines(content);
		assertEquals(1, lines.length);
		assertTrue(lines[0].contains("\"class\""));
		
	}
	
}
