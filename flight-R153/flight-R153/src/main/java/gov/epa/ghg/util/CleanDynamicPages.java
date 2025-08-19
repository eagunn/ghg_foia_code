package gov.epa.ghg.util;

import java.util.Vector;

public class CleanDynamicPages {
	
	private static Vector badV;
	
	static {
		badV = new Vector();
		badV.add("<!-->");
		badV.add("&lt;");
		badV.add("<A");
		badV.add("<ABBREV>");
		badV.add("<ACRONYM>");
		badV.add("<ADDRESS>");
		badV.add("<APPLET>");
		badV.add("<AREA>");
		badV.add("<AU>");
		badV.add("<AUTHOR>");
		badV.add("<B>");
		badV.add("<BANNER>");
		badV.add("<BASE>");
		badV.add("<BASEFONT>");
		badV.add("<BGSOUND>");
		badV.add("<BIG>");
		badV.add("<BLINK>");
		badV.add("<BLOCKQUOTE>");
		badV.add("<BQ>");
		badV.add("<BODY");
		badV.add("\r\n");
		badV.add("<CAPTION>");
		badV.add("<CENTER>");
		badV.add("<CITE>");
		badV.add("<CODE>");
		badV.add("<COL>");
		badV.add("<COLGROUP>");
		badV.add("<CREDIT>");
		badV.add("<DEL>");
		badV.add("<DFN>");
		badV.add("<DIR>");
		badV.add("<DIV>");
		badV.add("<DL>");
		badV.add("<DT>");
		badV.add("<DD>");
		badV.add("<EM>");
		badV.add("<EMBED>");
		badV.add("<FIG>");
		badV.add("<FN>");
		badV.add("<FONT>");
		badV.add("<FORM");
		badV.add("<FRAME>");
		badV.add("<FRAMESET>");
		badV.add("<H1>");
		badV.add("<H2>");
		badV.add("<H3>");
		badV.add("<H4>");
		badV.add("<H5>");
		badV.add("<H6>");
		badV.add("<HEAD>");
		badV.add("<HR>");
		badV.add("<HTML");
		badV.add("<I>");
		badV.add("<IFRAME");
		badV.add("<IMG");
		badV.add("<INPUT");
		badV.add("<INS>");
		badV.add("<ISINDEX>");
		badV.add("<KBD>");
		badV.add("<LANG>");
		badV.add("<LH>");
		badV.add("<LI>");
		badV.add("<LINK>");
		badV.add("<LISTING>");
		badV.add("<MAP>");
		badV.add("<MARQUEE>");
		badV.add("<MATH>");
		badV.add("<MENU>");
		badV.add("<META>");
		badV.add("<MULTICOL>");
		badV.add("<NOBR>");
		badV.add("<NOFRAMES>");
		badV.add("<NOTE>");
		badV.add("<OL>");
		badV.add("<OVERLAY>");
		badV.add("<P>");
		badV.add("<PARAM>");
		badV.add("<PERSON>");
		badV.add("<PLAINTEXT>");
		badV.add("<PRE>");
		badV.add("<Q>");
		badV.add("<RANGE>");
		badV.add("<SAMP>");
		badV.add("<SCRIPT>");
		badV.add("<SELECT>");
		badV.add("<SMALL>");
		badV.add("<SPACER>");
		badV.add("<SPOT>");
		badV.add("<STRIKE>");
		badV.add("<STRONG>");
		badV.add("<SUB>");
		badV.add("<SUP>");
		badV.add("<TAB>");
		badV.add("<TABLE>");
		badV.add("<TBODY>");
		badV.add("<TD>");
		badV.add("<TEXTAREA>");
		badV.add("<TEXTFLOW>");
		badV.add("<TFOOT>");
		badV.add("<TH>");
		badV.add("<THEAD>");
		badV.add("<TITLE>");
		badV.add("<TR>");
		badV.add("<TT>");
		badV.add("<U>");
		badV.add("<UL>");
		badV.add("<VAR>");
		badV.add("<WBR>");
		badV.add("<XMP>");
		badV.add("<script");
		badV.add("</script>");
		badV.add("</");
		badV.add("javascript");
		badV.add("expression");
		badV.add("alert");
		badV.add("alert(");
	}
	
	/**
	 * Replaces possibly malicious characters with their HTML escape equivalences.
	 * This is for sending text to the clients browser while trying to avoid unwanted
	 * side effects to occur due to possible tags embedded in the text.
	 */
	
	public static String cleanString(String in_string) {
		return ServiceUtils.nullSafeHtmlEscape(in_string);
	}
	
	public static String uncleanString(String in_string) {
		return ServiceUtils.nullSafeHtmlUnescape(in_string);
	}
	
	/**
	 * Strips unwanted HTML tags out of the supplied in_string so that it can safely be
	 * displayed in a browser.  Also replaces newline and carriage returns with &lt;br&gt; tags.
	 *
	 * @param in_string A string of characters to be rendered in clients browser.
	 *
	 * @return Scrubbed in_string
	 */
	public static String cleanHTMLtags(String in_string) {
		// in_string = cleanString(in_string);
		if (in_string == null) {
			return null;
		}
		if (in_string == "") {
			return in_string;
		}
		// Vector badV = new Vector();
		boolean v_found = false;
		// boolean cr_found = true;
		// badV.add("\r");
		//  badV.add("\n");
		v_found = true;
		while (v_found) {
			for (int f = 0; f < badV.size(); f++) {
				int i = in_string.toUpperCase().indexOf((String) badV.get(f).toString().toUpperCase());
				if (i >= 0) {
					String in_stringBefore = in_string.substring(0, i);
					String in_stringAfter = in_string.substring(i + badV.get(f).toString().length());
					// if you find \r\n, then repace with <br>
					if (badV.get(f).toString().toUpperCase().equals("\r\n") ||
							badV.get(f).toString().toUpperCase().equals("\r") ||
							badV.get(f).toString().toUpperCase().equals("\n")) {
						in_string = in_stringBefore + "<br>" + in_stringAfter;
					} else {
						in_string = in_stringBefore + in_stringAfter;
					}
				}
			}
			// check in_string again.
			v_found = false;
			for (int f = 0; f < badV.size(); f++) {
				int i = in_string.toUpperCase().indexOf((String) badV.get(f).toString().toUpperCase());
				if (i >= 0) {
					v_found = true;
				}
			}
			// finished checking
		}
		
		return in_string;
	}
}
