package com.cloudmine.coderunnerwrapper;

import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.JsonUtilities;
import com.cloudmine.coderunner.SnippetArguments;
import com.cloudmine.coderunner.SnippetContainer;
import com.cloudmine.coderunner.SnippetResponseConfiguration;
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

public class CodeRunnerRootServlet extends HttpServlet {
	private static final long serialVersionUID = 771578936675722864L;
    private static final Logger LOG = LoggerFactory.getLogger(CodeRunnerRootServlet.class);


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String snippetName = req.getRequestURI().substring(1); // remove the first slash
            LOG.info("doPost called for snippetName: " + snippetName);

            @SuppressWarnings("unchecked") Map<String, String[]> parameterMap = req.getParameterMap(); // pass the parameter map along to the snippet

            Map<String, SnippetContainer> snippetContainers = CodeSnippetNameServlet.getSnippetNamesToContainers();

            // Check if the snippet container is available based on the path, and activate it if so.
            // Otherwise render a 404 and stop.
            if (snippetContainers.containsKey(snippetName)) {
                LOG.info("Has key, calling snippet");
                Map<String, String> convertedParamMap = convertParameterMap(parameterMap);
                SnippetContainer container = snippetContainers.get(snippetName);

                String asyncString = convertedParamMap.get("async");
                boolean isAsync = asyncString != null && Boolean.parseBoolean(asyncString);
                long startTime = System.currentTimeMillis();
                if(isAsync) {
                    LOG.info("Running asynchronously");
                    RunnableSnippet snippet = new RunnableSnippet(container, new SnippetArguments(new SnippetResponseConfiguration(), convertedParamMap));
                    new Thread(snippet).start();
                } else {
                    LOG.info("Running synchronously, a response will be returned");
                    RunnableSnippet snippet = new RunnableSnippet(container, new SnippetArguments(new SnippetResponseConfiguration(), convertedParamMap), resp);
                    snippet.run();
                }
                long totalTime = System.currentTimeMillis() - startTime;
                LOG.info("Ran for: " + totalTime + "ms");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                resp.flushBuffer();
            }
        }catch(Throwable throwable) {
            LOG.error("Snippet crashed while running", throwable);
            SimpleCMObject errorObject = new SimpleCMObject(false);
            errorObject.add("error", throwable.getLocalizedMessage());
            JsonUtilities.writeObjectToJson(errorObject, resp.getOutputStream());
            resp.flushBuffer();
        }

    }

    private Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> convertedParamMap = new HashMap<String, String>();
        for(String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            String valueAsString = getValueAsString(values);
            LOG.info(key + ":" + valueAsString);
            convertedParamMap.put(key, valueAsString);
        }
        return convertedParamMap;
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
