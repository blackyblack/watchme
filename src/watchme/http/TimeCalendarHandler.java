package watchme.http;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    //timezone - contains information about local time eg summer/winter shift
    //timezone.summerTimeStart - time till summer time shift since the start of the year
    //timezone.winterTimeStart - time till winter time shift since the start of the year
    //timezone.summerTimeShift - how many msec should we add to the current time on the summer time shift
    //timezone.winterTimeShift - how many msec should we add to the current time on the winter time shift
    super("startTimestamp", "timelapseUnit", "timelapse", "repeat", "repeatTimes", "timezone");
  }

  @SuppressWarnings("unchecked")
  @Override
  JSONObject processRequest(HttpServletRequest req)
  {
    JSONObject answer = new JSONObject();
    JSONParser parser = new JSONParser();
    
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
      
      String timezoneStr = Convert.emptyToNull(req.getParameter("timezone"));
      JSONObject timezone = null;
      Long summerTimeStart = 0L;
      Long winterTimeStart = 0L;
      Long summerTimeShift = 0L;
      Long winterTimeShift = 0L;
      Long timezoneShift = 0L;
      
      if(timezoneStr != null)
      {
        try
        {
          timezone = (JSONObject) parser.parse(timezoneStr);
        }
        catch(Exception e)
        {
          answer.put("result", "error");
          answer.put("error", JSONResponses.incorrect("timezone"));
          return answer;
        }
        
        String timezoneShiftStr = Convert.emptyToNull((String) timezone.get("timezoneShift"));
        if(timezoneShiftStr != null)
        {
          try
          {
            timezoneShift = Convert.parseLong(timezoneShiftStr);
          }
          catch(IllegalArgumentException e)
          {
            answer.put("result", "error");
            answer.put("error", JSONResponses.incorrect("timezoneShift"));
            return answer;
          }
        }
        
        String summerTimeStartStr = Convert.emptyToNull((String) timezone.get("summerTimeStart"));
        if(summerTimeStartStr != null)
        {
          try
          {
            summerTimeStart = Convert.parseLong(summerTimeStartStr);
          }
          catch(IllegalArgumentException e)
          {
            answer.put("result", "error");
            answer.put("error", JSONResponses.incorrect("summerTimeStart"));
            return answer;
          }
        }
        
        String winterTimeStartStr = Convert.emptyToNull((String) timezone.get("winterTimeStart"));        
        if(winterTimeStartStr != null)
        {
          try
          {
            winterTimeStart = Convert.parseLong(winterTimeStartStr);
          }
          catch(IllegalArgumentException e)
          {
            answer.put("result", "error");
            answer.put("error", JSONResponses.incorrect("winterTimeStart"));
            return answer;
          }
        }
        
        String summerTimeShiftStr = Convert.emptyToNull((String) timezone.get("summerTimeShift"));
        if(summerTimeShiftStr != null)
        {
          try
          {
            summerTimeShift = Convert.parseLong(summerTimeShiftStr);
          }
          catch(IllegalArgumentException e)
          {
            answer.put("result", "error");
            answer.put("error", JSONResponses.incorrect("summerTimeShift"));
            return answer;
          }
        }
        
        String winterTimeShiftStr = Convert.emptyToNull((String) timezone.get("winterTimeShift"));        
        if(winterTimeShiftStr != null)
        {
          try
          {
            winterTimeShift = Convert.parseLong(winterTimeShiftStr);
          }
          catch(IllegalArgumentException e)
          {
            answer.put("result", "error");
            answer.put("error", JSONResponses.incorrect("winterTimeShift"));
            return answer;
          }
        }
      }

      TimeRecord eventSource = new TimeRecord();
      eventSource.whenTimestamp = startTimestamp;
      eventSource.timelapse = timelapse;      
      eventSource.repeat = repeat;
      eventSource.repeatTimes = repeatTimes;
      eventSource.timelapseUnit = timelapseUnit;
      eventSource.timezoneShift = timezoneShift;
      eventSource.summerTimeStart = summerTimeStart;
      eventSource.winterTimeStart = winterTimeStart;
      eventSource.summerTimeOffset = summerTimeShift;
      eventSource.winterTimeOffset = winterTimeShift;
      
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
