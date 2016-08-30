package com.github.basking2.sdsai;
/* $Id: LLTest.java 767 2008-09-03 13:37:20Z sam $ */

import org.junit.Test;

public class LLTest
{
    @Test
    public void main()
    {
        List<Integer> l = new LinkedList<Integer>();
        int  s = 0;


        for(int i = 0; i<10; i++){
            l.add(new Integer(i));
            s++;

            l.add(new Integer(-i));
            s++;

            Integer o = l.del(0);
            s--;

            System.out.println("E! I="+o + " size="+s +" l.size()="+l.size());

        }

    }
}
