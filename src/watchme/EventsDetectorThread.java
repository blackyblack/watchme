package watchme;

import watchme.util.Convert;
import watchme.util.Logger;

public class EventsDetectorThread extends Thread
{
  public boolean terminate = false;
  
  public void run()
  {
    Long before = Convert.now();
    
    while(true)
    {
      if(terminate) break;
      
      try
      {
        Long now = Convert.now();
        if((now - before) < AppConstants.EVENTS_UPDATE_PERIOD_S)
        {
          sleep(10000);
          continue;
        }
        
        try
        {          
          Logger.logMessage("Checking for events");
          detectEvents();
	        Logger.logMessage("Finished checking");
        }
        catch(Exception e1)
        {
        	Logger.logWarningMessage("EventsDetectorThread error: " + e1.getMessage());
        }
            
        before = now;
      }
      catch (InterruptedException e)
      {
        break;
      }
    }
    
    Logger.logMessage("EventsDetectorThread stopped");
  }
  
  private void detectEvents()
  {
    ///TODO: check DB for events sources
    ///TODO: check if event condition is met
    ///TODO: check for notification recepient
    ///TODO: send notification to recipient (put in queue)
    ///TODO: close finished event source
  }
}
