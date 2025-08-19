/**
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://probe.jstripe.com/d/license.shtml
 * <p>
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.jstripe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import lombok.extern.log4j.Log4j2;

/**
 * Silly JSP tag to display duration in milliseconds in hours:minutes:seconds
 *
 * Author: Vlad Ilyushchenko
 */

@Log4j2
public class DurationTag extends TagSupport {
	
	private long value;
	
	public void setValue(long value) {
		this.value = value;
	}
	
	public int doStartTag() throws JspException {
		try {
			int millis = (int) ((value / 1000 - Math.round(value / 1000)) * 1000);
			long sec = value / 1000;
			
			long mins = sec / 60;
			sec = sec % 60;
			
			long hours = mins / 60;
			mins = mins % 60;
			
			pageContext.getOut().write(long2Str(hours) + ":" + long2Str(mins) + ":" + long2Str(sec) + "." + millis);
		} catch (IOException e) {
			log.debug(e.getMessage());
			throw new JspException(e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	private String long2Str(long l) {
		return l < 10 ? "0" + l : Long.toString(l);
		
	}
}
