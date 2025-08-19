package gov.epa.ghg.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class TagSanitizingFilter implements Filter {
	
	private static List<String> ALLOW_URL = new ArrayList<String>();
	static {
		ALLOW_URL.add("/helpdesk/management/saveannouncement.ajx".toLowerCase());
	}
	
	public void init(FilterConfig arg0) throws ServletException {}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {		
	
		FilteredHttpServletRequest sanitizedRequest = 
				new FilteredHttpServletRequest((HttpServletRequest)request);
		
		String requestURI = sanitizedRequest.getRequestURI();
		requestURI = requestURI.replace(sanitizedRequest.getContextPath(), "");
		
		if(!ALLOW_URL.contains(requestURI.toLowerCase())) {
			sanitizedRequest.copyAndSanitizeOriginalMap(request.getParameterMap());
		}
		
		FilteredHttpServletResponse responseWrapper =
				new FilteredHttpServletResponse((HttpServletResponse)response);
		
		chain.doFilter(sanitizedRequest,responseWrapper);
		
		responseWrapper.rewriteCookieToHeader(sanitizedRequest, responseWrapper);

	}

	public void destroy() {}

}

class FilteredHttpServletRequest extends HttpServletRequestWrapper {

	private HashMap sanitizedParameters;

	public FilteredHttpServletRequest(HttpServletRequest nested) {
		super(nested);
		sanitizedParameters = new HashMap();
	}
	
	public void copyAndSanitizeOriginalMap(Map originalMap){
		sanitizedParameters.putAll(originalMap);
		
		// Sanitize the parameters
		Iterator iter = sanitizedParameters.values().iterator();
		while(iter.hasNext()){
			String[] tempArray = (String[])iter.next();
			for(int i=0;i<tempArray.length;i++) {
				tempArray[i] = CleanDynamicPages.cleanHTMLtags(tempArray[i]);
			    tempArray[i] = CleanDynamicPages.cleanString(tempArray[i]);
			}
		}
	}

	public String[] getParameterValues(String name){		
		return sanitizedParameters.get(name) == null ?
					super.getParameterValues(name) :
						(String[])sanitizedParameters.get(name);
	}

	public String getParameter(String name) {		
		return sanitizedParameters.get(name) == null ?
					super.getParameter(name) :
						((String[])sanitizedParameters.get(name))[0];	
	}
}

class FilteredHttpServletResponse extends HttpServletResponseWrapper {

	public FilteredHttpServletResponse(HttpServletResponse response) {
		super(response);
	}
	
	public void rewriteCookieToHeader(HttpServletRequest request, HttpServletResponse response) {
		if (response.containsHeader("Set-Cookie")) {
			String sessionid = request.getSession().getId();
			String contextPath = request.getContextPath();
			String secure = "";
			if (request.isSecure())	{
				secure = "; Secure";
			}
			response.setHeader("Set-Cookie", "JSESSIONID=" + sessionid + "; Path=" + contextPath + "; HttpOnly" + secure);
		}
	}
}