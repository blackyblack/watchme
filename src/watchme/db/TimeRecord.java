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
  
  //calculated fields
  
  //when should we generate event
  public BigInteger whenTimestamp;
  //how many repeat times left
  public Long repeatTimesLeft;

  public TimeRecord() {
    cancelable = new CancelableRecord();
    repeat = false;
    repeatTimes = 0L;
    repeatTimesLeft = 0L;
    timelapseUnit = TimeDB.timeUnits[0];
  }
}