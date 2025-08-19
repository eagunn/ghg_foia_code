package com.opensymphony.module.sitemesh.velocity;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.VelocityViewServlet;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.util.OutputConverter;

public class VelocityDecoratorServlet extends VelocityViewServlet {
	
	public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) {
		HTMLPage htmlPage = (HTMLPage) request.getAttribute(RequestConstants.PAGE);
		String template;
		
		context.put("base", request.getContextPath());
		
		// For backwards compatability with apps that used the old VelocityDecoratorServlet
		// that extended VelocityServlet instead of VelocityViewServlet
		context.put("req", request);
		context.put("res", response);
		
		if (htmlPage == null) {
			context.put("title", "Title?");
			context.put("body", "<p>Body?</p>");
			context.put("head", "<!-- head -->");
			template = request.getServletPath();
		} else {
			try {
				context.put("title", OutputConverter.convert(htmlPage.getTitle()));
			} catch (IOException e) {
				// ignore
			}
			{
				StringWriter buffer = new StringWriter();
				try {
					htmlPage.writeBody(OutputConverter.getWriter(buffer));
				} catch (IOException e) {
					// ignore
				}
				context.put("body", buffer.toString());
			}
			{
				StringWriter buffer = new StringWriter();
				try {
					htmlPage.writeHead(OutputConverter.getWriter(buffer));
				} catch (IOException e) {
					// ignore
				}
				context.put("head", buffer.toString());
			}
			context.put("page", htmlPage);
			DecoratorMapper decoratorMapper = getDecoratorMapper();
			Decorator decorator = decoratorMapper.getDecorator(request, htmlPage);
			template = decorator.getPage();
		}
		
		return getTemplate(template);
	}
	
	private DecoratorMapper getDecoratorMapper() {
		Factory factory = Factory.getInstance(new Config(getServletConfig()));
		DecoratorMapper decoratorMapper = factory.getDecoratorMapper();
		return decoratorMapper;
	}
}
