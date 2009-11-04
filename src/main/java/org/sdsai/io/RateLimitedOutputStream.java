package org.sdsai.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This output stream blocks when a send will exceed its rate
 * limit. 
 */
public class RateLimitedOutputStream extends OutputStream
{
  private long         _rateLimitBps;
  private double       _rateBps;
  private long         _lastSend;

  /**
   * Milliseconds over which to compute our sending rate average.
   */
  private long         _timePeriod = 1000;

  private OutputStream _outputStream;

  /**
   * This is the minimum amount of time that the system will pause if sending all the
   * data at once would violate the rate limit. This value must never be such that no
   * data is sent as that will cause thrashing. This value should also 
   * not be set too high as that will cause long pauses in sending.
   * 
   * Settable by the user or computed when {@link RateLimitedOutputStream#_burstSize} is set.
   */
  private long         _minimumPause;

  /**
   * This is the maximum size of a burst of traffic.
   *
   * Settable by the user or computed when {@link RateLimitedOutputStream#_minimumPause} is set.
   */
  private long         _burstSize;

  /**
   * Create a new output stream with a limit of Bps, bytes
   * per second. Sets the minimum delay to 1000ms using {@link RateLimitedOutputStream#setDelay(long)}.
   * This sets the burst size to the value of bps.
   * @param outputStream the output stream that will be throttled.
   * @param bps the rate of the stream in bytes per second.
   */
  public RateLimitedOutputStream(OutputStream outputStream, int bps)
  {
    this._outputStream  = outputStream;
    this._rateLimitBps  = bps;
    this._lastSend      = 0;

    setDelay(1000);
  }

  /**
   * Close the output stream referenced by this object.
   */
  public void close() throws IOException
  {
    _outputStream.close();
  }

  /**
   * Return the perceived rate at which this output stream has most recently sent
   * data. This is not an amortized value but a point-in-time computation. Because
   * output is done in bursts, this value can vary widely from the actual overall 
   * rate.
   * @return the current rate this object believes it is sending at over the period of time
   * set by {@link RateLimitedOutputStream#setBurstSize(long)} or 
   * {@link RateLimitedOutputStream#setDelay(long)}.
   */
  public double getRate()
  {
    return _rateBps;
  }

  /**
   * @return the rate limit.
   */
  public double getRateLimit()
  {
    return _rateLimitBps;
  }

  /**
   * Return change in time in milliseconds.
   * This will never be greater than _timePeriod and never less than zero.
   * Notice that this may return 0. getDeltaT(...)==0 is a special condition.
   * Keep an eye out of (deltaT==0)?...:...; statements in this class.
   * @return the change of time in milliseconds since the last recorded send operation.
   */
  private long getDeltaT(long timeNow)
  {
    long deltaT = timeNow - _lastSend;

    if ( deltaT > _timePeriod || deltaT < 0)
    {
      return _timePeriod;
    }
    else
    {
      return deltaT;
    }
  }

  /**
   * When the output stream must block before sending data it can
   * block the minimum time to send 1 byte, it can block the time
   * required to send all data, or it can behave in the middle.
   * This sets the delay and burst size of the OutputStream
   * using the burst size. The delay is then computed using
   * the current rate. 
   * 
   * @see RateLimitedOutputStream#setDelay(long)
   */
  public void setBurstSize(long bytes)
  {
    _burstSize    = bytes;
    _minimumPause = 1000 * bytes / _rateLimitBps;
  }

  /**
   * Similar to {@link RateLimitedOutputStream#setDelayedPayloadSize(int)}
   * this method limits the burst and delay of an output stream 
   * using the current rate and the submitted delay value in milliseconds.
   * 
   * @param millis
   * @see RateLimitedOutputStream#setDelay(long)
   */
  public void setDelay(long millis)
  {
    _burstSize    =  millis * 1000 * _rateLimitBps ;
    _minimumPause = millis;
  }

  /**
   * Set the period of time in milliseconds over which the average rate is computed.
   * Setting this to extremely low values may result in erroneous behavior 
   * because of mathematical rounding errors. A value of 1000 to 60000 is 
   * usually sufficient. The default is 1000 milliseconds (1 second).
   * @param timePeriod the number of milliseconds over which to compute the send rate of this OutputStream.
   */
  public void setTimePeriod(long timePeriod)
  {
    this._timePeriod = timePeriod;
  }

  /**
   * <p>Intelligent pausing function.
   * 
   * <p>Given the data remaining to be sent this will compute the
   * amount of time to sleep in order to send the remaining data
   * or sleep for the minimum pause amount required to send the
   * minimum block size. The shorter of the two values is preferred.
   * 
   * <p>This function assumes that the maximum number of bytes has just been sent.
   * @param dataLeft
   */
  private void throttlingSleep(int dataLeft)
  {

    long deltaT = getDeltaT(System.currentTimeMillis());

    long dataCouldSend = (deltaT == 0 )    ?
        
        // Subtract what we could possibly send right now.
        (long) (_rateLimitBps - _rateBps)  :
          
          _rateLimitBps * deltaT           ;

        // If we do not return in this if, we must sleep for some amount of time.
        if ( dataCouldSend > dataLeft )
          return;


        // Remove what we can send now from what we have to pause to send.
        dataLeft -= dataCouldSend;

        // How many milliseconds must we sleep to send the rest of our data?
        long pause = 1000 * dataLeft / _rateLimitBps ;

        // Don't pause too long. Keep data moving.
        if ( pause > _minimumPause || pause < 0 ) {
          pause = _minimumPause;

        } else if ( pause == 0 ) {
          // If we are in this branch we know 2 things:
          //    (1) we can't send all of our bytes in one shot and 
          //    (2) we can't compute a reasonable sleep amount because of math errors.
          //    A compromise is to sleep for just a moment and then send.
          pause = 1;
        }

        // Sleep for some period of time (exactness isn't that critical).
        synchronized(this)
        {
          try
          {
            // If pause < 1 this will be 0 and we will sleep forever.
            this.wait(pause);
          }
          catch(InterruptedException e)
          {
            // If we wake, that's OK. Rate limits 
            // are enforced assuming an unknowable actual sleep time.
          }
        }
  }

  public void write(byte[] b, int off, int len) throws IOException
  {
    while ( len > 0 )
    {

      // Pause to send the rest of the data.
      throttlingSleep(len);

      // Compute deltaT after a potential sleep.
      long deltaT = getDeltaT(System.currentTimeMillis());

      // Compute how many bytes we may send.
      long canSend = (deltaT > 0)           ?
          
          // Normally we just compute how many bytes may be sent.
          _rateLimitBps * deltaT / 1000     :
            
          // If no time change, accumulate the rate.
          (long) (_rateLimitBps - _rateBps) ;


      // Do not propose sending too much (if canSend is < 0 then underflow occurred).
      if ( canSend > _burstSize || canSend < 1 )
        canSend = _burstSize;

      // Send what we can. Maybe all of it! Never more.
      if ( canSend > len )
        canSend = len;

      // Write what we can now and flush the stream.
      _outputStream.write(b, off, (int) canSend);
      _outputStream.flush();

      // Update what's left.
      len -= canSend;
      off += canSend;

      // Timestamp to include transmission delay in further computations. Transmission is non-zero.
      long finishedSend = System.currentTimeMillis();

      // Compute how many milliseconds since we last sent something.
      deltaT = getDeltaT(finishedSend);

      // With our current rate stored, update the last time we sent data.
      _lastSend = finishedSend;

      _rateBps = (deltaT == 0)        ?

          // If no time has elapsed, increase our rate by what we've sent.
          _rateBps += canSend         :
          
          // Compute the new rate by summing a proportional amount of
          // the former rate with the complimentary proportion of the 
          // new rate. Essentially a weighted moving average using a 
          // time period to deduce the weighting.
          // NOTE: detalT is always <= _timePeriod. See getDeltaT().
          (_rateBps * ((_timePeriod - deltaT))+(1000*canSend)) / _timePeriod ;

    }
  }

  public void write(byte[] b) throws IOException
  {
    write(b, 0, b.length);
  }

  public void write(int b) throws IOException
  {
    write(new byte[] { (byte) b }, 0, 1);
  }
}
