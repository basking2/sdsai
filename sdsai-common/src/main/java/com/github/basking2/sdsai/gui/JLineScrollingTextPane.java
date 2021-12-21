/**
 * Copyright (c) 2008-2021 Sam Baskinger
 *
 * $Id: JLineScrollingTextPane.java 770 2008-09-03 21:39:19Z sam $
 */

package com.github.basking2.sdsai.gui;

import java.awt.Rectangle;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * This is almost identical to the JScrollingTextPane except
 * this does not use buffer size but line number as its
 * determiner of when to remove lines from the buffer.
 * As such, this can make now guarantees of size but is more
 * human friendly in that it only removes lines.  When 1 line is 
 * added which causes the total number of lines to be exceeded,
 * the last line is removed.
 */
public class JLineScrollingTextPane extends JScrollPane
{
  private static final long serialVersionUID = 1L;
  
  private JViewport      viewport;
  private JTextPane      textPane;
  private StyledDocument doc;

  /**
   * How many pixels from the bottom the scroll bar must be for auto-scrolling
   * to happen. 
   */
  private int threshold  = 64;

  /**
   * The scroll back size length of lines.
   */
  private int[] scrollback = new int[1000];

  /**
   * Which index in the scrollback array is the next empty slot.
   */
  private int currVal = -1;

  /**
   * This is the end of the list of values. When the list if full, nextVal
   * will equal lastVal. When empty this will equal -1.
   */
  private int lastVal = -1;

  public JLineScrollingTextPane(int width, int height)
  {
    textPane   = new JTextPane();
    viewport   = getViewport();
    doc        = textPane.getStyledDocument();

    textPane.setEditable(false);

    setViewportView(textPane);

    setSize(width, height);

  }

  public JLineScrollingTextPane()
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
   * The maximum number of lines this can hold.
   * Resetting this value destroys the current contents.
   */
  public void setScrollback(int s)
  {
    scrollback = new int[s]; 
    currVal = -1;
    lastVal = -1;

    try {
      doc.remove(0, doc.getEndPosition().getOffset());
    } catch(BadLocationException e){
    }

  }

  public int  getScrollback(){ return scrollback.length; }

  /**
   * Return the styled document for this so that styles may be added.
   */
  public StyledDocument getStyledDocument(){ return doc; }
  public JTextPane      getJTextPane()     { return textPane; }
  public JViewport      getJViewport()     { return viewport; }

  /**
   * Append what is considered a line.  Note that lines don't need to end in
   * a "\n". Lines are removed in the blocking by which they were added.
   * @param s string to append.
   */
  public void append(final String s)
  {
    if ( ! SwingUtilities.isEventDispatchThread() ) {
      
      final JLineScrollingTextPane jsl = this;
      
      SwingUtilities.invokeLater(
          new Runnable()
          {
            public void run()
            { 
              jsl.append(s); 
            } 
          } 
      );
    } else {
    
    boolean doscroll = willScroll();

    /**
     * If lastVal is -1, this is empty and we have to initilize values.
     */
    if(lastVal == -1){

      lastVal       = 0;
      currVal       = 0;
      scrollback[0] = s.length();

    } else {

      /* We use this tmp value to avoid an extra addition and not modify
       * currVal until AFTER the potential call of removeLine().  If
       * currVal == lastVal removeLine will assume the array is empty
       * when in this case it is full. */
      int tmpCurrVal = currVal + 1;

      /* Put tmpCurrVal into the range of the array. */
      if(tmpCurrVal == scrollback.length)
        tmpCurrVal = 0;

      if(tmpCurrVal == lastVal)
        removeLine();

      /* Increment currVal by setting it to tmpCurrVal. */
      currVal = tmpCurrVal;

      /* Set the length. */
      scrollback[currVal] = s.length();

    }

    try {
      doc.insertString(doc.getEndPosition().getOffset(), s, null);

      if(doscroll)
        scrollToBottom();

    } catch(BadLocationException e){
    }
    }
  }

  /**
   * Append what is considered a line.  Note that lines don't need to end in
   * a "\n". Lines are removed in the blocking by which they were added.
   * @param s string to append.
   * @param sty style to use.
   */
  public void append(final String s, final String sty)
  {
    
    if ( ! SwingUtilities.isEventDispatchThread() ) {
      
      final JLineScrollingTextPane jsl = this;
      
      SwingUtilities.invokeLater(
          new Runnable()
          {
            public void run()
            { 
              jsl.append(s, sty); 
            } 
          } 
      );
    } else {

    boolean doscroll = willScroll();

    /**
     * If lastVal is -1, this is empty and we have to initilize values.
     */
    if(lastVal == -1){

      lastVal       = 0;
      currVal       = 0;
      scrollback[0] = s.length();

    } else {

      /* We use this tmp value to avoid an extra addition and not modify
       * currVal until AFTER the potential call of removeLine().  If
       * currVal == lastVal removeLine will assume the array is empty
       * when in this case it is full. */
      int tmpCurrVal = currVal + 1;

      /* Put tmpCurrVal into the range of the array. */
      if(tmpCurrVal == scrollback.length)
        tmpCurrVal = 0;

      if(tmpCurrVal == lastVal)
        removeLine();

      /* Increment currVal by setting it to tmpCurrVal. */
      currVal = tmpCurrVal;

      /* Set the length. */
      scrollback[currVal] = s.length();

    }

    try {
      doc.insertString(doc.getEndPosition().getOffset(), s, doc.getStyle(sty));

      if(doscroll)
        scrollToBottom();

    } catch(BadLocationException e){
    }
    }
  }

  /**
   * Remove the last line if one exists.
   * @return A string is always returned. If there is no content the
   *         string "" is returned.
   */
  public String removeLine()
  {
    /* We put this up here so the compiler has chance to optimize this. */
    String s;

    /* If this is empty */
    if(lastVal == -1)
      return "";

    try {

      /* Get the text we will return. */
      s = doc.getText(0, scrollback[lastVal]);

      /* Remove the text from the document. */
      doc.remove(0, scrollback[lastVal]);

      /* Keep our array clean and zeroed. Can we remove this later as an opt? */
      scrollback[lastVal] = 0;

      /* Did we just remove the LAST line? */
      if(lastVal == currVal){

        lastVal = -1;
        currVal = -1;

      } else {

        lastVal++;

        /* loop around when we drop the last element in the array. */
        if(lastVal == scrollback.length)
          lastVal = 0;

      }

    } catch(BadLocationException e){

      s = "";

    }

    return s;
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
    
    if ( SwingUtilities.isEventDispatchThread() ) {
      
      viewport.validate();
      viewport.scrollRectToVisible(new Rectangle(0, textPane.getHeight(), 0, 0));
      
    } else {
      
      final JLineScrollingTextPane jsl = this;
      
      SwingUtilities.invokeLater(
          new Runnable()
          {
            public void run()
            { 
              jsl.scrollToBottom(); 
            } 
          } );
      
    }
  }

  public static void main(String[] argv)
  {
    //JLineScrollingTextPane j = 
    new JLineScrollingTextPane();
  }
}
