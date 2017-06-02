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
    Long now = 100L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.whenTimestamp = new BigInteger(now.toString());
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.now = 110L;
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
  }
  
  @Test
  public void testDetectFutureOnetimeEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = 100L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.whenTimestamp = new BigInteger(now.toString());
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.now = 90L;
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
  }
  
  @Test
  public void testDetectSimplePeriodicEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = 100L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.cancelable.creationTime = new BigInteger(now.toString());
    eventSource.repeat = true;
    eventSource.repeatTimes = 2L;
    eventSource.repeatTimesLeft = 2L;
    eventSource.timelapse = new BigInteger("1");
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.now = 90L;
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //time is in the past - do not start generating events
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 2L);
    
    TimeEventsDetector.now = 110L;
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //only one event is generated on each detectTimeEvents() call
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 1L);
    
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //all events are generated now since we requested 2 events
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 0L);
  }
  
  @Test
  public void testDetectSimpleCalendarEvent()
  {
    TimeDB.timeSourcesDb.clear();
    Long now = 100L;
    
    TimeRecord eventSource = new TimeRecord();
    eventSource.cancelable.creationTime = new BigInteger(now.toString());
    eventSource.repeat = true;
    eventSource.repeatTimes = 2L;
    eventSource.repeatTimesLeft = 2L;
    eventSource.timelapse = new BigInteger("1");
    eventSource.timelapseUnit = "day";
    
    //generate new event id and store it into event sources
    String eventId = TimeDB.newId();
    TimeDB.putTimeRecord(eventId, eventSource);
    
    TimeEventsDetector.now = 90L;
    TimeEventsDetector.detectTimeEvents();
    
    TimeRecord detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //time is in the past - do not start generating events
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 2L);
    
    //set to next day
    TimeEventsDetector.now = 1000L * 60L * 60L * 24L + 110L;
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //only one event is generated on each detectTimeEvents() call
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 1L);
    
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //we should wait another day to close the event
    Assert.assertEquals(detectedEventSource.cancelable.closed, false);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 1L);
    
    //set to next day
    TimeEventsDetector.now += (1000L * 60L * 60L * 24L);
    TimeEventsDetector.detectTimeEvents();
    
    detectedEventSource = TimeDB.getTimeRecord(eventId);
    
    //only one event is generated on each detectTimeEvents() call
    Assert.assertEquals(detectedEventSource.cancelable.closed, true);
    Assert.assertEquals(detectedEventSource.repeatTimesLeft, (Long) 0L);
  }
}
