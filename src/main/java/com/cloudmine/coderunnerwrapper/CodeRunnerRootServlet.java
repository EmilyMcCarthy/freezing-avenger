package com.cloudmine.coderunnerwrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;

import com.cloudmine.coderunner.SnippetContainer;

public class CodeRunnerRootServlet extends HttpServlet {
	private static final long serialVersionUID = 771578936675722864L;
	
	private Map<String, SnippetContainer> snippetContainers;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		if (snippetContainers == null) {
			snippetContainers = new HashMap<String, SnippetContainer>();
		}
		
		// TODO: make this configurable
		Reflections reflections = new Reflections("com.cloudmine.coderunner.examples");
		for (Class<? extends SnippetContainer> containerClass : reflections.getSubTypesOf(SnippetContainer.class)) {
			try {
				SnippetContainer container = containerClass.newInstance();
				snippetContainers.put(container.getSnippetName(), container);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String snippetName = req.getRequestURI().substring(1); // remove the first slash
		@SuppressWarnings("unchecked") Map<String, String[]> parameterMap = req.getParameterMap();
		if (snippetContainers.containsKey(snippetName)) {
			SnippetContainer container = snippetContainers.get(snippetName);
			resp.getWriter().print(container.runSnippet(parameterMap));
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		resp.flushBuffer();
	}
}
