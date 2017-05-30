package watchme;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import watchme.db.TimeDB;
import watchme.db.TimeRecord;

public class TimeEventsDetectorTest
{
  @Test
  public void testDetectOnetimeEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = System.currentTimeMillis();
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.whenTimestamp = new BigInteger(now.toString());
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
  }
  
  @Test
  public void testDetectFutureOnetimeEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = System.currentTimeMillis();
    now += 1000000L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.whenTimestamp = new BigInteger(now.toString());
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
  }
  
  @Test
  public void testDetectPastOnetimeEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = System.currentTimeMillis();
    now -= 1000000L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.whenTimestamp = new BigInteger(now.toString());
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
  }
  
  @Test
  public void testDetectSimplePeriodicEvent()
  {
    TimeDB.timeSourcesDb.clear();
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.repeat = true;
    eventSource.repeatTimes = 2L;
    eventSource.repeatTimesLeft = 2L;
    eventSource.timelapse = new BigInteger("1");
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 1L);
    
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 0L);
  }
}
