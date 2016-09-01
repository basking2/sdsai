package com.github.basking2.sdsai;


import java.util.LinkedList;
import java.util.List;

public class RedBlackTreeTest
{
  public static void main(String[] argv)
  {
    RedBlackTree<Integer> rbt = new RedBlackTree<Integer>();
    RedBlackTree<Integer>.RBNode node;

    int rounds = 1000;

    for ( int i = 0; i < rounds; i++ ) {
      rbt.add(new Key<Integer>(i, new Integer(i)));
    }

    for ( Key<Integer> k : rbt ) { 
      System.out.print(k.getData() +  " "); 
    }

    System.out.println("\n------------");

    int counter = 0;
    while ( rbt.size() > 0 ) {

      /* Pick a "random" key to remove. */
      int i = (int) ( Math.random() * 1000 ) ;

      /* Report on how often we guess at more keys than we have. */
      if ( counter % 1000 == 0 ) 
        System.out.print("C" + counter + " ");

      /* Report on keys we remove. */
      if ( rbt.del(new Key<Integer>(i)) != null )
        System.out.print("R" + i + " ");

      /* If we just can't remove all the keys, eventually bomb. */
      if ( counter == 1000000 ) {
        System.out.println("\nEither you are very statistically unlucky or the RedBlackTree is failing to remove some keys.");
        System.exit(1);
      }

      counter++;
    }

    for ( int i = 0; i < rounds; i++ ) {
      rbt.add(new Key<Integer>(i, new Integer(i))); }
    for ( int i = 0; i < rounds; i++ ) {
      rbt.add(new Key<Integer>(i, new Integer(i))); }
    for ( int i = 0; i < rounds; i++ ) {
      rbt.add(new Key<Integer>(i, new Integer(i))); }

    System.out.print("\n");
    node = rbt.first(new Key<Integer>(4));
    System.out.println("--- 4 = "+node.getKey().getData()+" ---");

    node = rbt.last(new Key<Integer>(4));
    System.out.println("--- 4 = "+node.getKey().getData()+" ---");


    findalltest(rbt, 1);
    findalltest(rbt, 2);
    findalltest(rbt, 3);
    findalltest(rbt, 4);
    findalltest(rbt, 100);
  }

  public static void findalltest(RedBlackTree<Integer> rbt, int i)
  {
    List<Key<Integer>> l = new LinkedList<Key<Integer>>();

    rbt.findAll(new Key<Integer>(i), l);

    for ( Key<Integer> ki : l )
      System.out.print(ki.getData()+"\t");

    System.out.print("\n");
  }
}
