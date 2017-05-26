package watchme;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import watchme.http.APIServlet;
import watchme.util.Logger;

public class Application
{
  public static final String version = AppConstants.APP_VERSION;
  public static Boolean terminated = false;
  public static EventsDetectorThread detector = new EventsDetectorThread();
  
  public static void main(String[] args)
  {
    Logger.logMessage("watchme " + version);
    
    terminated = false;
    
    //setup web access here    
    Server apiServer = new Server();
    // HTTP Configuration
    HttpConfiguration http_config = new HttpConfiguration();
    
    ServerConnector httpConnector = new ServerConnector(apiServer, 
        new HttpConnectionFactory(http_config));
    httpConnector.setName("unsecured"); // named connector
    httpConnector.setHost(AppProperties.host);
    httpConnector.setPort(AppProperties.webport);
    
    ServletContextHandler siteHandler = new ServletContextHandler();
    
    //API servlet
    siteHandler.addServlet(APIServlet.class, "/api");
    
    //allow CORS
    if (AppProperties.getBooleanProperty("watchme.apiServerCORS"))
    {
      FilterHolder filterHolder = siteHandler.addFilter(CrossOriginFilter.class, "/*", null);
      filterHolder.setInitParameter("allowedHeaders", "*");
      filterHolder.setAsyncSupported(true);
    }
    
    HandlerList apiHandlers = new HandlerList();
    apiServer.setConnectors(new Connector[] { httpConnector });

    apiHandlers.addHandler(siteHandler);
    apiServer.setHandler(apiHandlers);
    
    apiServer.setStopAtShutdown(true);
    try
    {
      apiServer.start();
    }
    catch (Exception e)
    {
      Logger.logMessage("Could not start API server", e);
      return;
    }
    
    Logger.logMessage("Started API server at " + AppProperties.host + ":" + AppProperties.webport);
    
    detector.start();
    
    try
    {
      while(true)
      {
        if(terminated) break;
        Thread.sleep(1000);
      }
    } 
    catch (InterruptedException e)
    {
      Logger.logWarningMessage("watchme terminated");
      terminated = true;
    }
    
    try
    {
      apiServer.stop();
    }
    catch (Exception e)
    {
      Logger.logMessage("Could not stop API server", e);
    }
    
    detector.terminate = true;
    try
    {
      detector.join();
    }
    catch (InterruptedException e)
    {
    }
    
    Logger.logMessage("watchme stopped");

  }

}
