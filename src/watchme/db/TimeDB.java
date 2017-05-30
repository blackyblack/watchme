package watchme.db;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import watchme.AppProperties;
import watchme.Constants;

public final class TimeDB
{
  static final TimeDB instance = new TimeDB();
  
  private TimeDB() {
  }
  
  static public String[] timeUnits = { "msec", "day", "month", "year" };
  static public Map<String, TimeRecord> timeSourcesDb = new HashMap<String, TimeRecord>();
  
  public static String newId()
  {
    Random rand = new Random();
    BigInteger idVal = new BigInteger(Constants.EVENT_ID_BITS.intValue(), rand);
    String id = idVal.toString();
    return id;
  }
  
  public static CancelableRecord getCancelable(String id)
  {
    if(id == null) return null;
    
    if(AppProperties.useDB)
    {      
      return null;
    }
    
    TimeRecord record1 = timeSourcesDb.get(id);
    if(record1 != null) return record1.cancelable;
    
    return null;
  }
  
  public static void setCancelable(String id, CancelableRecord cancelable)
  {
    if(id == null) return;
    
    if(AppProperties.useDB)
    {      
      return;
    }
    
    TimeRecord record1 = timeSourcesDb.get(id);
    if(record1 != null)
    {
      record1.cancelable = cancelable;
      TimeDB.putTimeRecord(id, record1);
      return;
    }
    
    return;
  }
  
  public static TimeRecord getTimeRecord(String id)
  {
    if(id == null) return null;
    
    if(AppProperties.useDB)
    {      
      return null;
    }
    
    TimeRecord record = timeSourcesDb.get(id);
    return record;
  }
  
  public static void putTimeRecord(String id, TimeRecord record)
  {    
    if(AppProperties.useDB)
    {
      return;
    }
    
    timeSourcesDb.put(id, record);
  }
  
  public static Set<String> findOpenedEvents()
  {    
    if(AppProperties.useDB)
    {      
      return new HashSet<String>();
    }
    
    Set<String> ids = new HashSet<String>();
    
    for(String a : timeSourcesDb.keySet())
    {
      if(timeSourcesDb.get(a).cancelable.closed) continue;
      ids.add(a);
    }
    
    return ids;
  }
}
