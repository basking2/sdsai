/* $Id: JScrollingLog.java 752 2008-08-06 21:03:46Z sam $ */

package com.github.basking2.sdsai.gui;

import java.util.Enumeration;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class JScrollingLog extends JLineScrollingTextPane
{

  private static final long serialVersionUID = 1L;
  
  private Handler handler = new Handler()
  {
    Formatter formatter = new SimpleFormatter();

    public void publish(LogRecord r)
    {
      append(formatter.format(r));
    }

    public void close()
    {
    }

    public void flush()
    {
    }
  };

  /**
   * Create a log monitor whose handler is added to logger s.
   */
  public JScrollingLog(String s)
  {
    addToLogger(s);
  }

  /** 
   * Create a logger with its handlers not added to any logs.
   */
  public JScrollingLog()
  {
  }

  /**
   * Remove this object's handler from all loggers.
   */
  public void removeFromAllLoggers()
  {
    LogManager logman   = LogManager.getLogManager();
    Enumeration<String> logEnum = logman.getLoggerNames();

    while(logEnum.hasMoreElements())
      logman.getLogger((String)logEnum.nextElement()).removeHandler(handler);   
  }

  /**
   * Add this object's handler to all loggers.
   */
  public void addToAllLoggers()
  {
    LogManager logman   = LogManager.getLogManager();
    Enumeration<String> logEnum = logman.getLoggerNames();

    while(logEnum.hasMoreElements())
      logman.getLogger((String)logEnum.nextElement()).addHandler(handler);   
  }

  /**
   * Remove this object's handler from the given logger.
   */
  public void removeFromLogger(String s)
  {
    LogManager.getLogManager().getLogger(s).removeHandler(handler);   
  }

  /**
   * Remove this object's handler from the given logger.
   */
  public void addToLogger(String s)
  {
    LogManager.getLogManager().getLogger(s).addHandler(handler);   
  }

  public void finalize(){
    removeFromAllLoggers();
  }
}
