/* $Id: BTreeNextPrevTest.java 767 2008-09-03 13:37:20Z sam $ */

package com.github.basking2.sdsai;

import org.junit.Test;

public class BTreeNextPrevTest
{
    @Test
    public void btreeNextPrevTest()
    {

        BTree<String> bt = new BTree<String>();

        Key<String> tmpKey;

        for ( int i = 0; i < 100; i++ ) {
            String s = "" + i;
            tmpKey = new Key<String>(s, s);
            bt.add(tmpKey);
        }

        BTree<String>.State bts = bt.minLocation();

        for ( int i = 0; i < 20; i++ )
            countUpNDown(bts, i);

    }

    private static void countUpNDown(BTree<String>.State bts, int count)
    {
        for ( int i = 0; i < count; i++ ) {
            System.err.print(bts.getKey() + " ");
            bts = bts.next();
        }

        System.err.print("| ");

        bts = bts.prev();

        for ( int i = 0; i < count; i++ ) {
            System.err.print(bts.getKey() + " ");
            bts = bts.prev();
        }

        System.err.print("\n");
    }

}
