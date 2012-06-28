package com.cloudmine.coderunnerwrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;

import com.cloudmine.coderunner.SnippetContainer;
import com.cloudmine.coderunner.SnippetResponseConfiguration;

public class CodeRunnerRootServlet extends HttpServlet {
	private static final long serialVersionUID = 771578936675722864L;

	private Map<String, SnippetContainer> snippetContainers;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if (snippetContainers == null) {
			snippetContainers = new HashMap<String, SnippetContainer>();
		}

		Reflections reflections = new Reflections("com", "net", "org", "me", "io", "edu", "gov", "mil");
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
		@SuppressWarnings("unchecked") Map<String, String[]> parameterMap = req.getParameterMap(); // pass the parameter map along to the snippet
		SnippetResponseConfiguration responseConfig = new SnippetResponseConfiguration();

		// Check if the snippet container is available based on the path, and activate it if so.
		// Otherwise render a 404 and stop.
		if (snippetContainers.containsKey(snippetName)) {
			SnippetContainer container = snippetContainers.get(snippetName);
			Object snippetResponse = container.runSnippet(responseConfig, parameterMap);

			resp.setContentType(responseConfig.getMimeType());

			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(resp.getOutputStream(), snippetResponse);
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		resp.flushBuffer();
	}
}
