/* $Id: TopBottomGraph.java 303 2006-04-04 01:30:15Z sam $ */

package com.github.basking2.sdsai.gui;

import java.awt.*;
import java.awt.image.*;

public class TopBottomGraph extends BufferedImage
{
 /**
  * This is the offset from the y axis at which we draw our next data sample.
  */
  private  int offset = 0;

  private  int prevTop    = 0;
  private  int prevBottom = 0;

  private Color topColor    = new Color(0xd5, 0xbe, 0x00, 0xff);
  private Color bottomColor = new Color(0x00, 0x00, 0x77, 0xff);
  private Color bgColor     = Color.white ;
  private Color markerColor = Color.black ;

 /**
  * Creates an image of type TYPE_INT_ARGB of size width x height.
  *
  * @param width The width of the image.
  * @param height The height of the image.
  */
  public TopBottomGraph(int width, int height)
  {
    super(width, height, TYPE_INT_ARGB);
    createGraphics().fillRect(0, 0, width, height);
  }

 /**
  * Plot two graphs. One along the top of the image and one along the bottom.
  * Note that top + bottom should not exceed the hight of the graph.
  * Also note that the value is the distance from the top or bottom. Thus, if
  * bot are set to 0, the graph plots a point along the top and bottom of the
  * graph.  It's a good hack to make one line go away.
  *
  * @param bottom The bottom point.
  * @param top The top point.
  */
  public void plot(int bottom, int top)
  {
    Graphics2D g = createGraphics();
    Polygon p1;                      /* bottom graph. */
    Polygon p2;                      /* top graph. */

    int height = getHeight();
    int width  = getWidth();

    /* We can only do this so simply because the graphic thingy clips. Yay! */
    if(offset == width)
      offset = -1;

    int[] x = new int[4];
    int[] y = new int[4];

    x[0] = offset;
    x[1] = offset;
    x[2] = offset + 1;
    x[3] = offset + 1;

    y[0] = height;
    y[1] = height - prevBottom;
    y[2] = height - bottom;
    y[3] = height;

   /** Debug.
    * for(int i=0; i<4; i++)
    *  System.out.print("("+x[i]+", "+y[i]+")");
    * System.out.print(" height: "+height + "\n");
    */

    p1 = new Polygon(x, y, 4);

    y[0] = 0;
    y[1] = prevTop;
    y[2] = top;
    y[3] = 0;

   /** Debug.
    * for(int i=0; i<4; i++)
    *   System.out.print("("+x[i]+", "+y[i]+")");
    * System.out.print(" height: "+height + "\n");
    */

    p2 = new Polygon(x, y, 4);

    g.setBackground(bgColor);
    g.clearRect(offset, 0, 1, height);

    g.setColor(bottomColor);
    g.fill(p1);
    g.setColor(topColor);
    g.fill(p2);

    offset++;
    prevTop    = top;
    prevBottom = bottom;

    /* The last thing we do is draw a black mark where we are going next. */
    g.setColor(markerColor);
    g.drawLine(offset, 0, offset, height);
  }

 /**
  * Plot the percentages on this graph.
  *
  * @param bottom The percentage from the bottom of the graph.
  * @param top The percentage from the top of the graph.
  */
  public void plot(float bottom, float top)
  {
    int h = getHeight();
    plot((int)(bottom*h), (int)(top*h));
  }

 /**
  * Plot given the <i>differences</i> between the values instead of the absolute values.
  *
  * @param dbottom Change from the previous bottom.
  * @param dtop Change from the previous top.
  */
  public void plotDelta(int dbottom, int dtop)
  {
    plot(prevBottom + dbottom, prevTop + dtop);
  }

 /**
  * Plot given the <i>differences</i> in percentages of the total height.
  * The plotted data is computed as 
  * <code>new_value = previous_value + graph_height * delta_percentage</code>.
  *
  * @param dbottom Change from the previous bottom.
  * @param dtop Change from the previous top.
  */
  public void plotDelta(float dbottom, float dtop)
  {
    int h = getHeight();
    plot(prevBottom + (int)(h*dbottom), prevTop + (int)(h*dtop));
  }

 /**
  * This is for testing but we can pull it out later when we are comfy with it.
  *
  * @param argv Arguments.
  */
  public static void main(String[] argv)
  { 
    javax.swing.JFrame f = new javax.swing.JFrame("Test");

    TopBottomGraph tbg = new TopBottomGraph(1000, 200);

    TrivialImageCanvas tic = new TrivialImageCanvas(tbg);

    f.setSize(1000, 200);

    f.getContentPane().add(tic);

    f.setVisible(true);

    int val = 0;

    while(true){

      val = (int)((0.95 * val) + (0.05 * Math.random() * 200));

      tbg.plot(val, val);

      /* Repaint (inefficiently) the newly updated region. */
      tic.repaint(tbg.getOffset()-1, 0, 2, tbg.getHeight());

      try { 
        synchronized(f){ f.wait(20); } 
      } catch(InterruptedException e){
      }
    }
  }

 /**
  * @param c Set the color of the top graph to this.
  */
  public void setTopColor(Color c){ topColor = c; }

 /**
  * @param c Set the color of the bottom graph to this.
  */
  public void setBottomColor(Color c){ bottomColor = c; }

 /**
  * @param c Set the color of the background of the graph to this.
  */
  public void setBackgroundColor(Color c){ bgColor = c; }

 /**
  * @param c Set the color of the marker line which shows the front of the graph to this.
  */
  public void setMarkerColor(Color c){ markerColor = c; }

  /**
   * Get the offset in the graph from the y axis of the image.  This is useful
   * if a user wants to only repaint a section of the graph.
   *
   * @return The offset.
   */
  public int getOffset(){ return offset; }

 /**
  * Set the offset of the graph. If o is &lt; -1 or &gt;= the width it is set to -1.
  *
  * @param o The offset.
  */
  public void setOffset(int o)
  { 
    if(o >= getWidth() || o < -1)
      offset = -1;
    else
      offset = o;
  }
}
