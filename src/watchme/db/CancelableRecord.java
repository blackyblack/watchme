package watchme.db;

import java.math.BigInteger;

public class CancelableRecord
{
  public boolean closed;
  //when it was created
  public BigInteger creationTime;
  //when it was closed or generated
  public BigInteger closeTime;
  //who closed it
  public String closedBy;
  //why it was closed
  public String closeReason;
  
  public CancelableRecord() {
    closed = false;
    creationTime = new BigInteger(Long.toString(System.currentTimeMillis()));
  }
}