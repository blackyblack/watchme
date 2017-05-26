package watchme.http;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import watchme.db.CancelableRecord;
import watchme.db.TimeDB;
import watchme.util.Convert;

public final class CancelHandler extends APIServlet.APIRequestHandler {
  static final CancelHandler instance = new CancelHandler();

  private CancelHandler() {
    //id - unique ID of the event
    super("id");
  }

  @SuppressWarnings("unchecked")
  @Override
  JSONObject processRequest(HttpServletRequest req)
  {
    JSONObject answer = new JSONObject();
    
    try
    {      
      //this is unique ID of the event source
      String id = Convert.emptyToNull(req.getParameter("id"));
      
      if(id == null)
      {
        answer.put("result", "error");
        answer.put("error", JSONResponses.missing("id"));
        return answer;
      }
      
      CancelableRecord eventSource = TimeDB.getCancelable(id);
      
      //check ID existence
      if(eventSource == null)
      {
        answer.put("result", "error");
        answer.put("error", "No event with ID = " + id + " found");
        return answer;
      }
   
      eventSource.closed = true;
      eventSource.closedBy = "Owner";
      eventSource.closeReason = "Canceled";
      eventSource.closeTime = new BigInteger(Long.toString(System.currentTimeMillis()));

      //close event source
      TimeDB.setCancelable(id, eventSource);
      return answer;
    }
    catch(Exception e)
    {
      answer.put("result", "error");
      answer.put("error", e.getMessage());
      return answer;
    }
  }
}
