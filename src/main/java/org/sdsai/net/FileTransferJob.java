/* $Id: FileTransferJob.java 635 2008-04-21 18:40:59Z sbaskin $ */

package org.sdsai.net;

import org.sdsai.jobproc.*;
import java.io.*;

public abstract class FileTransferJob implements Job
{

  protected OutputStream    ops;


  public abstract long    start();
  public abstract long    process();
  public abstract void    sleep();
  public abstract boolean isDone();
}
