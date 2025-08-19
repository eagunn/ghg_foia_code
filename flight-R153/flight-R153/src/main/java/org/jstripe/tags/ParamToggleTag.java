/**
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://probe.jstripe.com/d/license.shtml
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.jstripe.tags;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.bind.ServletRequestUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ParamToggleTag extends TagSupport {

    private String param = "size";

    public int doStartTag() throws JspException {
        boolean getSize = ServletRequestUtils.getBooleanParameter((HttpServletRequest) pageContext.getRequest(), param, false);
        StringBuffer query = new StringBuffer();
        query.append(param).append("=").append(!getSize);
        for (Enumeration en = pageContext.getRequest().getParameterNames(); en.hasMoreElements(); ){
            String name = (String) en.nextElement();
            if (!param.equals(name)) {
                query.append("&").append(name).append("=").append(ServletRequestUtils.getStringParameter((HttpServletRequest) pageContext.getRequest(), name, ""));
            }
        }
        try {
            pageContext.getOut().print(query);
        } catch (IOException e) {
            log.debug(e);
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
