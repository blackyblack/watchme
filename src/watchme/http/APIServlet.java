package watchme.http;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import watchme.util.Convert;
import watchme.util.Logger;


public class APIServlet extends HttpServlet {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  static final Map<String,APIRequestHandler> apiRequestHandlers;
  
  abstract static class APIRequestHandler {

    private final List<String> parameters;

    APIRequestHandler(String... parameters) {
        this.parameters = Collections.unmodifiableList(Arrays.asList(parameters));
    }

    final List<String> getParameters() {
        return parameters;
    }

    abstract JSONObject processRequest(HttpServletRequest request) throws Exception;

}
  
  static {
    Map<String,APIRequestHandler> map = new HashMap<>();
    
    map.put("cancel", CancelHandler.instance);
    map.put("time", TimeCalendarHandler.instance);
    
    apiRequestHandlers = Collections.unmodifiableMap(map);
  }
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      process(req, resp);
  }
  
  @SuppressWarnings("unchecked")
  private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException 
  {
    resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
    resp.setHeader("Pragma", "no-cache");
    resp.setDateHeader("Expires", 0);

    JSONObject response = new JSONObject();

    try
    {
      if (!"POST".equals(req.getMethod()))
      {
        response.put("result", "error");
        response.put("error", JSONResponses.POST_REQUIRED);
        return;
      }
      
      String command = Convert.emptyToNull(req.getParameter("requestType"));
      APIRequestHandler apiRequestHandler = apiRequestHandlers.get(command);
      response = apiRequestHandler.processRequest(req);
    }
    catch (Exception e) 
    {
      Logger.logMessage("Error processing API request", e);
      response.put("result", "error");
      response.put("error", JSONResponses.ERROR_INCORRECT_REQUEST);
    }
    finally
    {
    	resp.setContentType("application/json");
      try (Writer writer = resp.getWriter())
      {
        response.writeJSONString(writer);
      }
    }
  }
}
