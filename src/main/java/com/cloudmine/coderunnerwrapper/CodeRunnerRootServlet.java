package com.cloudmine.coderunnerwrapper;

import com.cloudmine.coderunner.SnippetArguments;
import com.cloudmine.coderunner.SnippetContainer;
import com.cloudmine.coderunner.SnippetResponseConfiguration;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CodeRunnerRootServlet extends HttpServlet {
	private static final long serialVersionUID = 771578936675722864L;
    private static final Logger LOG = LoggerFactory.getLogger(CodeRunnerRootServlet.class);
	private Map<String, SnippetContainer> snippetContainers;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if (snippetContainers == null) {
			snippetContainers = new HashMap<String, SnippetContainer>();
		}

		Reflections reflections = new Reflections("com", "net", "org", "me", "io", "edu", "gov", "mil");
        Set<Class<? extends SnippetContainer>> subTypesOf = reflections.getSubTypesOf(SnippetContainer.class);
        LOG.info("Found " + subTypesOf.size() + " subtypes");

        for (Class<? extends SnippetContainer> containerClass : subTypesOf) {
			try {
				SnippetContainer container = containerClass.newInstance();
                String snippetName = container.getSnippetName();
                LOG.info("Storing snippetName: " + snippetName + " to: " + container);
                snippetContainers.put(snippetName, container);
			} catch (Exception e) {
				LOG.error("Trouble putting in container", e);
			}
		}
	}

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String snippetName = req.getRequestURI().substring(1); // remove the first slash
        LOG.info("doPost called for snippetName: " + snippetName);
        @SuppressWarnings("unchecked") Map<String, String[]> parameterMap = req.getParameterMap(); // pass the parameter map along to the snippet

        SnippetResponseConfiguration responseConfig = new SnippetResponseConfiguration();

        // Check if the snippet container is available based on the path, and activate it if so.
        // Otherwise render a 404 and stop.
        if (snippetContainers.containsKey(snippetName)) {
            LOG.info("Has key, calling snippet");
            Map<String, String> convertedParamMap = new HashMap<String, String>();
            for(String key : parameterMap.keySet()) {
                String[] values = parameterMap.get(key);
                String valueAsString = getValueAsString(values);
                LOG.info(key + ":" + valueAsString);
                convertedParamMap.put(key, valueAsString);
            }
            SnippetContainer container = snippetContainers.get(snippetName);
            Object snippetResponse = container.runSnippet(new SnippetArguments(responseConfig, convertedParamMap));
            resp.setContentType(responseConfig.getMimeType());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getOutputStream(), snippetResponse);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        resp.flushBuffer();
    }

    private String getValueAsString(String[] values) {
        switch(values.length) {
            case 0:
                return "";
            case 1:
                return values[0];
            default:
                LOG.error("String array received; should only receive one String per parameter. Concatenating");
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < values.length; i++) {
                    builder.append(values[i]);
                }
                return builder.toString();
        }
    }
}
