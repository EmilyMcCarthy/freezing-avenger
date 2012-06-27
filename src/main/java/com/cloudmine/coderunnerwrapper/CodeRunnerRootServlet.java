package com.cloudmine.coderunnerwrapper;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;

public class CodeRunnerRootServlet extends HttpServlet {
	private static final long serialVersionUID = 771578936675722864L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Reflections ref = null;

		// resp.getWriter().println("Hello Servlet World!");
	}
}
