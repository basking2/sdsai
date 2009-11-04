/* $Id: Connector.java 303 2006-04-04 01:30:15Z sam $ */

package org.sdsai.net;

import java.io.*;

/**
 * This is simple class builds a connection for a FileTransferJob.
 * The streams should be positioned so that nothing buy bytes of the file
 * flow in and out.
 */
public abstract class Connector
{
  
  protected InputStream  input;
  protected OutputStream output;

  public InputStream getInputStream()
  {
    return input;
  }

  public OutputStream getOutputStream()
  {
    return output;
  }

  public abstract void connect();

}
