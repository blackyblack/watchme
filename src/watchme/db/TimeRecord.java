package watchme.db;

import java.math.BigInteger;

public class TimeRecord
{
  public CancelableRecord cancelable;
  //should we repeat?
  public boolean repeat;
  //how many times should we repeat
  public Long repeatTimes;
  //what is the timelapse when we repeat
  public BigInteger timelapse;
  //in what units should we measure timelapse
  public String timelapseUnit;
  //user timezone (msec since GMT)
  public Long timezoneShift;
  //summer/winter time shift
  public Long summerTimeStart;
  public Long summerTimeOffset;
  public Long winterTimeStart;
  public Long winterTimeOffset;
  
  //calculated fields
  
  //when should we generate event
  public BigInteger whenTimestamp;
  //how many repeat times left
  public Long repeatTimesLeft;
  //if next summer or winter shift is required
  public boolean summerTimeShiftRequired;
  public boolean winterTimeShiftRequired;

  public TimeRecord() {
    cancelable = new CancelableRecord();
    repeat = false;
    repeatTimes = 0L;
    repeatTimesLeft = 0L;
    timelapseUnit = TimeDB.timeUnits[0];
    timezoneShift = 0L;
    summerTimeStart = 0L;
    summerTimeOffset = 0L;
    winterTimeStart = 0L;
    winterTimeOffset = 0L;
    
    summerTimeShiftRequired = true;
    winterTimeShiftRequired = true;
  }
}