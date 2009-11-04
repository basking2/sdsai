/* $Id: Util.java 281 2005-12-29 22:59:47Z sam $ */

package org.sdsai;

/**
 * OLD MESSY CODE.
 * This is due for a clean-up, though this may 
 * never happen as the behavior is correct.
 */

public class Util {
  
  public static int strToInt(String c)
  {
    int r       = 0,     /* The result */
      i         = 0;     /* An iterator */
    boolean neg = false; /* Is the number negative */
    
    if(c.charAt(0)=='-'){ /* Is this a signed number */
      neg=true;
      i++;
    } else if(c.charAt(0)=='+')
      i++;
    
    /* "main" loop of the conversion. */
    while(i<c.length() && c.charAt(i)>='0' && c.charAt(i)<='9'){
      r=(r*10)+(int)c.charAt(i)-48; /* 48 is the value of ASCII '0' */
      i++;
    }

    if(i==c.length())
      return neg?-r:r;
    else
      return 0;
  }

  public static double strToReal(String c){
    double r = 0;  /* our result */
    int i    = 0,  /* A common iterator */
      d    = 10; /* Decimal place of the number */
    
    boolean neg = false; /* Is the num negative */

    if(c.charAt(0)=='-'){ /* Is this number signed? */
      neg=true;
      i++;
    } else if(c.charAt(0)=='+')
      i++;
    
    while(i<c.length() && c.charAt(i)>='0' && c.charAt(i)<='9'){
      r=(r*10)+(int)c.charAt(i)-48; /* 48 is the value of ASCII '0' */
      i++;
    }

    if(i<c.length() && c.charAt(i) == '.')
      i++;
    else
      return 0;

    while(i<c.length() && c.charAt(i)>='0' && c.charAt(i)<='9') {
      r = r + ((int)c.charAt(i)-48)/(double)(d); /* 48 = ASCII '0'*/
      d=d*10; /* move to the next decimal place */
      i++;
    }
    
    if(i==c.length())
      return neg?-r:r;
    else
      return 0;
  } // end str_to_real
}//end class

