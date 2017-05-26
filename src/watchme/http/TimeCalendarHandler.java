package watchme.http;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import watchme.db.TimeDB;
import watchme.db.TimeRecord;
import watchme.util.Convert;

public final class TimeCalendarHandler extends APIServlet.APIRequestHandler {
  static final TimeCalendarHandler instance = new TimeCalendarHandler();

  private TimeCalendarHandler() {
    //startTimestamp - when should we start generating events
    //timelapseUnit - calendar unit to measure timelapse eg day, year
    //timelapse - how many calendar units to wait before next event 
    //repeat - should we repeat event.
    //repeatTimes - how many times should we repeat
    super("startTimestamp", "timelapseUnit", "timelapse", "repeat", "repeatTimes");
  }

  @SuppressWarnings("unchecked")
  @Override
  JSONObject processRequest(HttpServletRequest req)
  {
    JSONObject answer = new JSONObject();
    
    try
    {
      String timelapseStr = Convert.emptyToNull(req.getParameter("timelapse"));
      BigInteger timelapse = BigInteger.ZERO;      
      if(timelapseStr != null)
      {
        try
        {
          timelapse = new BigInteger(timelapseStr);
        }
        catch(NumberFormatException e)
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("timelapse"));
          return answer;
        }
      }
      
      //take initial event time 
      String startTimestampStr = Convert.emptyToNull(req.getParameter("startTimestamp"));
      BigInteger startTimestamp = BigInteger.ZERO;      
      if(startTimestampStr != null)
      {
        try
        {
          startTimestamp = new BigInteger(startTimestampStr);
        }
        catch(NumberFormatException e)
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("startTimestamp"));
          return answer;
        }
      }
      
      //either startTimestamp or timelapse should present
      if((startTimestampStr == null) && (timelapseStr == null))
      {
        answer.put("result", "error");
        answer.put("error", JSONResponses.missing("startTimestamp", "timelapse"));
        return answer;
      }
      
      //set startTimestamp to now() if not set in parameters
      if(startTimestampStr == null)
      {
        startTimestamp = new BigInteger(Long.toString(System.currentTimeMillis()));
      }
      
      String repeatStr = Convert.emptyToNull(req.getParameter("repeat"));
      boolean repeat = false;
      if(repeatStr != null)
      {
        try
        {
          repeat = Convert.parseBoolean(repeatStr);
        }
        catch(IllegalArgumentException e)
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("repeat"));
          return answer;
        }
      }
      
      String repeatTimesStr = Convert.emptyToNull(req.getParameter("repeatTimes"));
      Long repeatTimes = 0L;
      if(repeatTimesStr != null)
      {
        try
        {
          repeatTimes = Convert.parseLong(repeatTimesStr);
        }
        catch(IllegalArgumentException e)
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("repeatTimes"));
          return answer;
        }
      }
      
      //timelapse should be present if we repeat
      if(repeat && (timelapseStr == null))
      {
        answer.put("result", "error");
        answer.put("error", JSONResponses.missing("timelapse"));
        return answer;
      }
      
      String timelapseUnit = Convert.emptyToNull(req.getParameter("timelapseUnit"));
      
      if(timelapseUnit != null)
      {
        timelapseUnit.toLowerCase();
        
        Set<String> unitsSet = new HashSet<String>(Arrays.asList(TimeDB.timeUnits));
            
        if(!unitsSet.contains(timelapseUnit))
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("timelapseUnit"));
          return answer;
        }
      }

      TimeRecord eventSource = new TimeRecord();
      eventSource.whenTimestamp = startTimestamp;
      eventSource.timelapse = timelapse;      
      eventSource.repeat = repeat;
      eventSource.repeatTimes = repeatTimes;
      eventSource.timelapseUnit = timelapseUnit;
      
      //generate new event id and store it into event sources
      String eventId = TimeDB.newId();
      TimeDB.putTimeRecord(eventId, eventSource);
      
      answer.put("result", "ok");
      answer.put("id", eventId);
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
