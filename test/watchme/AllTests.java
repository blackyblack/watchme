package watchme;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import watchme.TimeEventsDetectorTest;

@RunWith(Suite.class)
@SuiteClasses({
  TimeEventsDetectorTest.class,
  })
public class AllTests
{

}
