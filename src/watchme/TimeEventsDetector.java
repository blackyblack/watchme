package watchme;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import watchme.db.TimeDB;
import watchme.db.TimeRecord;
import watchme.util.Logger;

public class TimeEventsDetector
{
  public static Long now = 0L;
  public static void detectTimeEvents()
  {
    ///TODO: check for notification recepient
    ///TODO: send notification to recipient (put in queue)
    ///TODO: close finished event source
    
    ///TODO: filter events by time
    Set<String> ids = TimeDB.findOpenedEvents();
    
    for(String a : ids)
    {
      TimeRecord x = TimeDB.getTimeRecord(a);
      
      if(!x.repeat)
      {
        ///HACK: one-time events only come in absolute timestamp format
        if(x.whenTimestamp.compareTo(new BigInteger(now.toString())) >= 1)
        {
          continue;
        }
        
        ///TODO: proper event notification
        Logger.logInfoMessage("Event notification!");
          
        ///TODO: close event when it is delivered
        x.cancelable.closed = true;
        x.cancelable.closedBy = "Owner";
        x.cancelable.closeReason = "Ready";
        x.cancelable.closeTime = new BigInteger(now.toString());
        TimeDB.putTimeRecord(a, x);
        continue;
      }
      
      //now we want to check timelapse property and apply timezone settings
      
      BigInteger whenTime = x.cancelable.creationTime;
      if((x.timelapseUnit == null) || (x.timelapseUnit.length() == 0) || (x.timelapseUnit.equals(TimeDB.timeUnits[0])))
      {
        //timelapse in msec - simply apply it
        whenTime = x.cancelable.creationTime.add(x.timelapse);
      }
      else
      {
        //otherwise should apply local time settings
        Calendar cal = Calendar.getInstance();
        
        //create timezone with own settings
        ///HACK: timezone offset is int - remember to check it on the API side
        TimeZone zone = TimeZone.getTimeZone("GMT");
        zone.setRawOffset(x.timezoneShift.intValue());
        //create custom calendar from it
        cal.setTimeZone(zone);
        
        //calculate summer/winter shift
        
        cal.setTimeInMillis(now);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Long startOfYear = cal.getTimeInMillis();
        cal.setTimeInMillis(x.cancelable.creationTime.longValue());
        
        ///HACK: timelapse is limited by int - remember to check it on the API side or find a way to increase it

        Long timesPassed = x.repeatTimes - x.repeatTimesLeft;
        
        if(x.timelapseUnit.equals("day"))
        {
          cal.add(Calendar.DATE, (int) ((timesPassed + 1) * x.timelapse.longValue()));
        }
        else if(x.timelapseUnit.equals("month"))
        {
          cal.add(Calendar.MONTH, (int) ((timesPassed + 1) * x.timelapse.longValue()));
        }
        else if(x.timelapseUnit.equals("year"))
        {
          cal.add(Calendar.YEAR, (int) ((timesPassed + 1) * x.timelapse.longValue()));
        }
        else
        {
          ///TODO: close event on wrong timelapseUnit
        }
        
        Long currentTime = cal.getTimeInMillis();
        
        if(x.summerTimeShiftRequired && (x.summerTimeStart > 0) && ((currentTime - startOfYear) >= x.summerTimeStart))
        {
          x.summerTimeShiftRequired = false;
          x.winterTimeShiftRequired = true;
          
          zone.setRawOffset(x.summerTimeOffset.intValue());
          cal.setTimeZone(zone);          
        }
        
        if(x.winterTimeShiftRequired && (x.winterTimeStart > 0) && ((currentTime - startOfYear) >= x.winterTimeStart))
        {
          x.winterTimeShiftRequired = false;
          x.summerTimeShiftRequired = true;
              
          zone.setRawOffset(x.winterTimeOffset.intValue());
          cal.setTimeZone(zone);          
        }

        whenTime = new BigInteger("" + currentTime);
      }
      
      String whenTimeStr = whenTime.toString();
      
      //time is not reached - skip it
      if(whenTime.compareTo(new BigInteger(now.toString())) >= 1)
      {
        continue;
      }
      
      ///TODO: check for time margin - do not send notification if too much time has passed since
      ///TODO: proper event notification
      Logger.logInfoMessage("Event notification!");
      
      if(x.repeatTimesLeft > 0)
      {
        x.repeatTimesLeft--;
      }
      
      //repeatTimes over
      if(x.repeatTimesLeft <= 0)
      {
        x.cancelable.closed = true;
        x.cancelable.closedBy = "Owner";
        x.cancelable.closeReason = "Ready";
        x.cancelable.closeTime = new BigInteger(now.toString());
      }
      
      TimeDB.putTimeRecord(a, x);
      continue;
    }
  }
}
