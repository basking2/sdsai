/* $Id: TrivialImageCanvas.java 670 2008-05-02 20:42:07Z sbaskin $ */

package org.sdsai.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * This is a simple class for displaying an image.
 */
public class TrivialImageCanvas extends JPanel //Canvas
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Image image;

  private int x;
  private int y;
  private int w;
  private int h;

  public TrivialImageCanvas(Image i, int x, int y, int w, int h)
  {
    image  = i;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;

    Dimension d = new Dimension(w, h);

    setMinimumSize(d);
    setPreferredSize(d);
  }

  public TrivialImageCanvas(Image i)
  {
    image = i;
    x = 0;
    y = 0;
    w = i.getWidth(this);
    h = i.getHeight(this);

    Dimension d = new Dimension(w, h);

    setMinimumSize(d);
    setPreferredSize(d);
  }

  public void paint(Graphics g)
  {
    g.drawImage(image, x, y, w, h, this);
  }

  /**
   * This is defined to handle information about the image should that
   * information not be available at the moment the constructor is called.
   */
  public boolean imageUpdate(Image img,
      int infoflags,
      int x,
      int y,
      int w,
      int h)
  {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    setSize(w, h);

    return false;
  }
}
