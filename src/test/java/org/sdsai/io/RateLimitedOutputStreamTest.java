package org.sdsai.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class RateLimitedOutputStreamTest
{
    private Logger _logger;
    
    /** 
     * A bit-bucket. Throw it all away.
     */
    private OutputStream _sink = new OutputStream()
    {
        public void write(int b) throws IOException { ; }
        public void write(byte[] b) throws IOException { ; }
        public void write(byte[] b, int off, int len) throws IOException { ; }
    };

    
    public RateLimitedOutputStreamTest()
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%r %C{1} %m%n")));
        _logger = Logger.getLogger(RateLimitedOutputStreamTest.class);
    }
    
    @DataProvider(name="rateTestDP")
    public Object[][] rateTestDP()
    {
        return new Object[][]
        {
                        // rate,             time
                        //{  1024,               1  },
                        //{  1024,               5  },
                        {  1024,               30 },
                        {  10240,              30 },
                        {  20480,              30 },
                        {  51200,              30 },
                        {  102400,             30 },
                        {  102400000,          30 },
                        {  1024000000,         30 }, 
                        {  1,                  10 },
        };
    }
    
    @Test(testName="rateTest", dataProvider="rateTestDP", groups={"perf"}, enabled=false)
    public void rateTest(int rate, int time)
    throws IOException
    {
        byte[] buffer      = new byte[102400];
        long   written     = 0;
        double allowedSkew = 0.10; // percent "fudge" room allowed.
        RateLimitedOutputStream r = new RateLimitedOutputStream(_sink, rate);
        
        r.setDelay(1000);
        
        long startTime = System.currentTimeMillis();
        
        for ( startTime = System.currentTimeMillis() ;
              time*1000 > System.currentTimeMillis() - startTime;
            )
        {
            int i = (int)(Math.random()*buffer.length);
            
            if ( i < 10 )
            {
                i = 10;
            }
            
            r.write(buffer, 0, i);
            written += i;
        }
        
        long stopTime = System.currentTimeMillis();
        
        
        double apparentRate = (written/((double)(stopTime - startTime)/1000));
        
        _logger.info(String.format("Apparent: %5.2f Instant: %5.2f Requested: %d", apparentRate, r.getRate(), rate));
        
        // Ensure that our overall computed value is close enough.
        Assert.assertTrue(Math.abs(apparentRate - rate) <= (rate * allowedSkew), 
                        "Err%: "+(Math.abs(apparentRate - rate)/(rate * allowedSkew))+"  Apparent("+apparentRate+") vs. "+rate);
        
        // Ensure that our perceived rate in our class is close enough.
        // There are many real-world examples where this will trend much lower, but here it 
        // should be close.
        Assert.assertTrue(Math.abs(r.getRate()  - rate) <= (rate * allowedSkew), 
                        "Err%: "+(Math.abs(r.getRate()  - rate)/(rate * allowedSkew))+"  Computed("+r.getRate()+") vs. "+rate);
        
        
    }
    
    /**
     * This test does not check that the rate limit is enforced
     * because sending at a rate of Integer.MAX_VALUE bytes per second
     * is more than most workstations can accomplish in the JVM. This is
     * a functional check to make sure that there are now rounding errors that cause
     * failure or deadlock.
     * 
     * There is a minimum rate test which should be checked against machine load if failure is observed.
     */
    @Test(enabled=false)
    public void sendingWithWayToHighDatarateTest()
    throws IOException
    {
        byte[] buffer = new byte[10240];
        long   written = 0;
        int rate = Integer.MAX_VALUE;
        RateLimitedOutputStream r = new RateLimitedOutputStream(_sink, rate);
        
        long startTime = System.currentTimeMillis();
        
        for ( startTime = System.currentTimeMillis() ;
              10000 > System.currentTimeMillis() - startTime;
            )
        {
            int i = (int)(Math.random()*buffer.length);
            r.write(buffer, 0, i);
            written += i;
        }
        
        long stopTime = System.currentTimeMillis();
        
        double apparentRate = (written/((stopTime - startTime)/1000));
        
        _logger.info("Rate appears to be " +  apparentRate);

        // In the very odd case that we can send faster than
        // Integer.MAX_VALUE bytes-per-second, check that we
        // aren't sending "too fast."
        if ( rate < apparentRate )
        {
            Assert.assertTrue(
                            Math.abs(apparentRate-rate) <= (rate*0.20)
                            );
        }
        else
        {
            // We should have sent at LEAST this fast, or assume there is
            // some logic error that is hanging the system.
            Assert.assertTrue(apparentRate > 1024000000);
        }
    }
}
