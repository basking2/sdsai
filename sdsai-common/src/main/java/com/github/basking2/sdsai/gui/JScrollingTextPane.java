/* $Id: JScrollingTextPane.java 675 2008-05-13 21:41:02Z sbaskin $ */

package com.github.basking2.sdsai.gui;

import java.awt.Rectangle;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class JScrollingTextPane extends JScrollPane
{
  private static final long serialVersionUID = 1L;
  
  private JViewport         viewport;
  private JTextPane         textPane;
  private StyledDocument    doc;

  /**
   * How many pixels from the bottom the scroll bar must be for auto-scrolling
   * to happen. 
   */
  private int threshold  = 64;

  /**
   * The scroll back size length at which the buffer is halved.
   */
  private int scrollback = 2000;

  public JScrollingTextPane(int height, int width)
  {
    textPane   = new JTextPane();
    viewport   = getViewport();
    doc        = textPane.getStyledDocument();

    textPane.setEditable(false);

    setViewportView(textPane);

    setSize(height, width);

  }

  public JScrollingTextPane()
  {
    textPane   = new JTextPane();
    viewport   = getViewport();
    doc        = textPane.getStyledDocument();

    setViewportView(textPane);

    textPane.setEditable(false);
  }

  /**
   * Set the threshold. This is the number of pixels from the bottom of the
   * window at which auto-scrolling stops.
   */
  public void setThreshold(int t){ threshold = t; }

  /**
   * Get the threshold. This is the distance from the bottom of the window at
   * which auto scrolling stops.
   */
  public int  getThreshold()     { return threshold; }

  /**
   * This is the maximum length that of the scroll back buffer in characters.
   * When this value is reached the buffer is cut in half.
   * The default value is 2000 (garanteeing 1000 characters will be 
   * retained at all times) and a value of 0 means do not cut this in half. 
   */
  public void setScrollback(int s){ scrollback = s; }
  public int  getScrollback(){ return scrollback; }

  /**
   * Return the styled document for this so that styles may be added.
   */
  public StyledDocument getStyledDocument(){ return doc; }
  public JTextPane      getJTextPane()     { return textPane; }
  public JViewport      getJViewport()     { return viewport; }

  /**
   * @param s string to append.
   * @param sty style to use.
   */
  public void append(String s, String sty)
  {
    boolean doscroll = willScroll();

    boolean truncate = scrollback > 0 && 
    doc.getLength() + s.length() >= scrollback;

    try {
      doc.insertString(doc.getEndPosition().getOffset(), s, doc.getStyle(sty));

      if(truncate)
        doc.remove(0, scrollback / 2);

      if(doscroll)
        scrollToBottom();

    } catch(BadLocationException e){
    }
  }

  public void append(String s)
  {
    boolean doscroll = willScroll();

    boolean truncate = doc.getLength() + s.length() >= scrollback;

    try {
      doc.insertString(doc.getEndPosition().getOffset(), s, null);

      if(truncate)
        doc.remove(0, scrollback / 2);

      if(doscroll)
        scrollToBottom();

    } catch(BadLocationException e){
    }
  }


  /**
   * Returns if the window is in a position to scroll or not.
   */
  public boolean willScroll()
  {
    JScrollBar jsb   = getVerticalScrollBar();
    return jsb.getMaximum() - jsb.getValue() - jsb.getVisibleAmount() < threshold;  
    //return jsb.getMaximum() - jsb.getValue() < threshold;  
  }

  public void scrollToBottom()
  {
    viewport.validate();
    viewport.scrollRectToVisible(new Rectangle(0, textPane.getHeight(), 0, 0));
  }
}
