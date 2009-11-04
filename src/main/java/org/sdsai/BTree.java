/* $Id: BTree.java 752 2008-08-06 21:03:46Z sam $ */

package org.sdsai;

/**
 *
 */
public class BTree<E>
{

  protected int  size;
  protected Node root;
  protected int  keymn; /* Smallest number of keys. */
  protected int  keymx; /* Largest number of keys. */


  /**
   * This class stores the location in a tree. If add or delete is called on
   * the tree, this object is irrelevant and behaviour with it is undefined.
   */
  public class State 
  {

    protected Node   node   = null;
    protected int    index  = 0;
    protected State  parent = null;

    protected State(State p, Node n, int i)
    {
      parent = p;
      node   = n;
      index  = i;
    }

    protected State(Node n, int i)
    {
      node   = n;
      index  = i;
    }

    public void trace()
    {
      traceHelper();
      System.err.print("\n");
    }

    public void traceHelper()
    {
      if ( parent != null )
        parent.traceHelper();

      System.err.print(getKey()+" ");
    }

    public Key<E> getKey()
    {
      return node.key[index];
    }

    /**
     * This returns a state that is the location of the minimum key in the
     * subtree represented by the node in this State with a chain of parent
     * references leading back to this State. That last step in the parent 
     * chain * before this State is a child of this State.
     */
    public State min()
    {
      State  s = this;
      Node   n = node;

      while ( ! n.isLeaf() ) {

        n = n.child[0];
        s = new State(s, n, 0);

      }

      return s;
    }

    /**
     * This returns a state that is the location of the maximum key in the
     * subtree represented by the node in this State with a chain of parent
     * references leading back to this State. That last step in the parent 
     * chain * before this State is a child of this State.
     */
    public State max()
    {
      State  s = this;
      Node   n = node;

      while ( ! n.isLeaf() ) {

        int lastNode = n.keycount();
        n = n.child[lastNode];
        s = new State(s, n, lastNode - 1);

      }

      return s;
    }

    /**
     * Returns a state for the next location or null if this State is
     * located at the end of the tree.
     * Typically a new state is created but if a state is encountered
     * that already exists, then that state may be used. 
     */
    public State next()
    {

      if ( node.isLeaf() ) {

        /* If it is possible to move to the right... */
        if ( index < node.keycount() - 1 ) {

          return new State(parent, node, index + 1);

          /* Else we have to return to the parent until we find on
           * in which the previou node is the left child of the key. */
        } else {

          Node  prevn  = node;   /* Previous Node. */
          State state  = parent; /* State for consideration. */

          while ( state != null ) {

            /** 
             * When state.node.child[state.index+1] == node it means that 
             * the node is to the right of the key (greater than). 
             * When state.node.child[state.index] == node it means that
             * the node is to the left of the key (less than). 
             *
             * When we find one that is less than, the next state is
             * the one we are examining.
             */
            if ( state.node.child[state.index] == prevn ) {

              return state;

            } else if ( state.index < state.node.keycount() - 1 ) {

              state.index++;
              return state;

            } else {

              prevn = state.node;
              state = state.parent;

            }

          }

          /* If we get here, there are no states in our path from the root
           * that satisfy the requirement for next(...). */
          return null;

        }

        /**
         * Here the State's node is not a leaf node. 
         * We must get the minimum of the index+1 child. 
         */
      } else {

        /* Get the min of the next subtree. */

        /* Make a new state ponting to the right child of this node. */
        State s = new State(this, node.child[index+1], 0);

        while ( ! s.node.isLeaf() ) {
          s = new State(s, s.node.child[0], 0);
        }

        return s;
      }

    }

    public State prev()
    {

      if ( node.isLeaf() ) {

        /* If it is possible to move to the left... */
        if ( index > 0 ) {

          return new State(parent, node, index - 1);

          /* Else we have to return to the parent until we find on
           * in which the prevous node is the right child of the key. */
        } else {

          Node  prevn  = node;   /* Previous Node. */
          State state  = parent; /* State for consideration. */

          while ( state != null ) {

            /** 
             * When state.node.child[state.index+1] == node it means that 
             * the node is to the right of the key (greater than). 
             * When state.node.child[state.index] == node it means that
             * the node is to the left of the key (less than). 
             *
             * When we find one that is less than, the next state is
             * the one we are examining.
             */
            if ( state.node.child[state.index+1] == prevn ) {

              return state;

              /* If we can return the max of a previous tree, do so. */
            } else if ( state.index > 0 ) {

              state.index--;
              return state;

            } else {

              prevn = state.node;
              state = state.parent;

            }

          }

          /* If we get here, there are no states in our path from the root
           * that satisfy the requirement for next(...). */
          return null;

        }

        /**
         * Here the State's node is not a leaf node. 
         * We must get the minimum of the index+1 child. 
         */
      } else {

        /* Get the max of the next subtree. */

        /* Make a new state ponting to the left child of this node. */
        State s = new State(this, node.child[index], 
            node.child[index].keycount()-1);

        /* Get the maximum child. */
        while ( ! s.node.isLeaf() ) {
          int lastNode = s.node.keycount();
          int lastKey  = s.node.child[lastNode].keycount() - 1;
          s = new State(s, s.node.child[lastNode], lastKey);
        }

        return s;
      }
    }

  }

  /*****************************************************************
   * How a tree is represented.
   * The advantages to this internal class are:
   *  - Truely empty data structure state.<br>
   *  - We can avoid recursion when "appropriate."<br>
   *  - We can compute and save global info like tree size.<br>
   */
  private class Node
  {
    protected BTree<E>    source;

    /** Contains key value and associated data. */
    protected Key<E>[]    key;

    protected Node[]   child;

    @SuppressWarnings("unchecked")
    public Node()
    {
      key   = new Key [2 * keymn - 1];
      child = new BTree.Node[2 * keymn];
    }

    public void print(){ printH(0, ""); }

    public void printH(int d, String prefix)
    {
      int i = 0;

      for(; i<keymx; i++){
        if(child[i]!=null){
          System.err.println(d + ", " + i + prefix + "---C  ");
          child[i].printH(d+1, prefix+    "     |");
        } else
          System.err.println(d + ", " + i + prefix + "-cnil ");
        if(key[i]!=null)
          System.err.println(d + ", " + i + prefix + "---" + key[i]);
        else
          System.err.println(d + ", " + i + prefix + "-knil ");
      }

      if(child[i]!=null){
        System.err.println(d + ", " + i + prefix + "---C  ");
        child[i].printH(d+1, prefix+    "     |");
      } else
        System.err.println(d + ", " + i + prefix + "-cnil ");
    }

    public Key<E> getKey(State s)
    {
      return s.node.key[s.index];
    }

    public State minLocation()
    {
      Node  n = this;
      State s = new State(n, 0); /* Init to the smallest root key. */

      while ( ! n.isLeaf() ) {
        n = n.child[0];
        s = new State(s, n, 0);
      }

      return s;
    }

    public State maxLocation()
    {
      Node  n = this;
      int   i = n.keycount();
      State s = new State(n, i - 1);

      while ( ! n.isLeaf() ) {
        n = n.child[i];             /* Next child. */
        i = n.keycount();           /* Max child in that child. */

        s = new State(s, n, i - 1); /* Max key in this child. */
      }

      return s;
    }

    /**
     * Insert a key at index i
     */
    public void insert(Key<E> k, int i)
    {

      for(int j = keymx - 1; j > i; j--)
        key[j] = key[j-1];


      key[i] = k;
    }

    /**
     * Insert a child at index i.
     */
    public void insert(Node c, int i)
    {

      for(int j = keymx; j > i; j--)
        child[j] = child[j-1];

      child[i] = c;
    }

    public Key<E> removeKey(int i)
    {
      Key<E> k = key[i];

      for(; i < keymx - 1; i++)
        key[i] = key[i+1];

      /* Set last element to null. */
      key[i] = null;

      return k;
    }

    public Node removeChild(int i)
    {
      Node c = child[i];

      for(; i < keymx; i++)
        child[i] = child[i+1];

      /* Set last element to null. */
      child[i] = null;

      return c;
    }

    public void chkTree()
    {
      chk();

      for(int i=0; i<keymx; i++){
        if(child[i]!=null){
          child[i].chk();
          child[i].chkTree();
        }
      }
    }

    /**
     * Sanity check this node.
     */
    public void chk()
    {
      @SuppressWarnings("unused")
      int j;

      if(this != root && key[keymn-2]==null){
        System.err.println("Underfill");
        print();
        j = 1/(1*0);
      }

      /* special case */
      if(key[0] == null && child[0]!=null){
        print();
        j = 1 / (1*0);
      }

      /* For all keys but the last one. */
      for(int i=0; i<keymx-2; i++){
        if(key[i] == null && key[i+1] != null){
          print();
          i = 1 / (1*0);
        }
      }

      /* For all keys. */
      for(int i=0; i<keymx-1; i++){
        if(!isLeaf()){

          /* Check that in non-leaf nodes, children line up w/ keys. */
          if((key[i]!=null && (child[i]==null || child[i+1]==null)) ||
              (key[i]==null && child[i+1]!=null)){

            String k, c1, c2;
            if(key[i]==null)
              k="key["+i+"] is null.";
            else
              k="key["+i+"] is not null.";

            if(child[i] == null)
              c1 = "child["+i+"] equals null";
            else 
              c1 = "child["+i+"] doesn't equals null";

            if(child[i+1] == null)
              c2 = "child["+(i+1)+"] equals null";
            else 
              c2 = "child["+(i+1)+"] doesn't equals null";

            System.err.println(k+ "\n" + c1 + "\n" + c2);
            print();
            i = 1/(1*0);
          }
        } else {
          if(key[i] != null && (child[i]!=null || child[i+1]!=null)){
            print();
            i = 1/(1*0);
          }
        }
      }
    }

    private void split(int childi)
    {
      /* Split node */
      Node snode = child[childi];

      /* our new node! */
      Node nnode = new Node();

      /* Put key and new child in place. */
      insert(snode.key[keymn - 1], childi);

      /* Insert the child ABOVE the key. */
      /* The code is more readable if you put it below, but we save
       * 2 assignment operations if we just do things this way. */
      insert(nnode, childi+1);

      /* Make sure to remove this reference. */
      snode.key[keymn - 1] = null; 

      /* Copy high order stuff from snode to loworder nnode indicies. */
      for(int i = 0; i < keymn - 1; i++){
        nnode.child[i]       = snode.child[i+keymn];
        nnode.key[i]         = snode.key[i+keymn];

        snode.child[i+keymn] = null;
        snode.key[i+keymn]   = null;
      }

      /* Copy the one extra child over. */
      nnode.child[keymn - 1] = snode.child[keymx];
      snode.child[keymx]     = null;

    }

    public boolean isLeaf(){ return child[0] == null; }

    public boolean isFull(){ return key[keymx-1] != null; }

    public boolean hasExtraKeys(){ return key[keymn-1] != null; }

    /** Has room to merge with another node. */
    public boolean canMerge(){ return key[keymn-1] == null; }

    /**
     * Returns the number of keys contained in this Node.  
     */
    public int keycount()
    {
      int i ;
      for(i = keymx - 1; i >= 0 && key[i] == null; i--)
        ;

      return i+1;
    }

    /**
     * Return the index of the key in the child that is 
     * equal or greater than k.
     */
    public int whichsubtree(Key<? extends Object> k)
    {

      int i=0;

      while(i < keymx && key[i] != null && key[i].lt(k))
        i++;

      return i;
    }

    public Key<E> delmax() { return del(max()); }

    public Key<E> max()
    {
      Node n = this;

      /* Take largest child tree */
      while( ! n.isLeaf())
        n = n.child[n.keycount()];

      //System.err.println("MAX: "+n.key[n.keycount()-1]);

      return n.key[n.keycount()-1];
    }

    Key<E> delmin(){ return del(min()); }    

    /**
     * Return the min key.
     */
    Key<E> min()
    {
      Node n = this;

      while( ! n.isLeaf())
        n = n.child[0];

      //System.err.println("MAX: "+n.key[n.keycount()-1]);

      return n.key[0];
    }

    /**
     * Has one more comparison per loop, but the code is much more
     * compact and readable. 
     */
    public Key<E> find(Key<? extends Object> key)
    {
      Node n = this;
      Key<E>  k = null;

      while(k==null && n != null){

        for(int i=0; i<keymx; i++){
          if(n.key[i] == null || key.lt(n.key[i])){

            n = n.child[i];
            i = keymx;      /* i=keymx breaks us out of the loop. */
            /* Shouldn't we use break here??? */

          } else if(key.eq(n.key[i])){

            k = n.key[i];
            i = keymx;      /* i=keymx breaks us out of the loop. */
            /* Shouldn't we use break here??? */

          } else if(i == keymx - 1){

            n = n.child[keymx];

          }
        }
      }

      return k;
    }

    /** 
     * Merge childi and childi+1 
     * NOTE: We should only merge when each 
     * child has keymn-1 keys in it.
     */
    public void merge(int childi)
    {
      int  k;

      /**
       * Remove parent key and child. 
       * Put key and node into the destination node.
       */
      Key<E>  key  = removeKey(childi);
      Node node = removeChild(childi+1);

      child[childi].key[keymn-1] = key;

      for(k=0; k < keymn - 1; k++){
        child[childi].key[keymn+k]   = node.key[k];
        child[childi].child[keymn+k] = node.child[k];
      }

      child[childi].child[keymn+k]   = node.child[k];

    }

    public void add(Key<E> key)
    {
      Node n = this;
      int  i;

      /* Iteratively find where to insert this node */
      while( ! n.isLeaf()){

        for(i = 0; i < keymx; i++){

          if(n.key[i] != null){

            if(key.lte(n.key[i])){


              /* Before descending into a node, conditionally split */
              if(n.child[i].isFull()){

                n.split(i);

                /* Recheck the new key.  Which tree do we descend into? */
                n = n.child[key.lte(n.key[i])?i:++i];

              } else {
                /* We found a tree we must descend to insert this key */
                n = n.child[i];
              }

              /* Break out of the loop gracefully */
              i = keymx;
            }
          } else {

            /* Before descending into a node, conditionally split */
            if(n.child[i].isFull()){

              n.split(i);

              /* Recheck the new key.  Which tree do we descend into? */
              n = n.child[key.lte(n.key[i])?i:++i];

            } else {

              /* We've run out of keys.  Descend down the tree */
              n = n.child[i];

            }

            /* Break out of the loop gracefully */
            i = keymx;
          }
        }
      }

      /* At this point we must insert into this node. */

      i=0; 

      while(n.key[i] != null && key.gt(n.key[i]))
        i++;

      n.insert(key, i);
    }

    public void addUnique(Key<E> key) throws DuplicateDataException
    {
      Node n = this;
      int  i;

      /* Iteratively find where to insert this node */
      while( ! n.isLeaf()){

        for(i = 0; i < keymx; i++){

          if(n.key[i] != null){

            if(key.lt(n.key[i])){

              /* Before descending into a node, conditionally split */
              if(n.child[i].isFull()){

                n.split(i);

                /* Recheck the new key.  Which tree do we descend into? */
                n = n.child[key.lte(n.key[i])?i:++i];

              } else {
                /* We found a tree we must descend to insert this key */
                n = n.child[i];
              }

              /* Break out of the loop gracefully */
              i = keymx;
            } else if(key.eq(n.key[i]))
              throw new DuplicateDataException(n.key[i]);

          } else {

            /* Before descending into a node, conditionally split */
            if(n.child[i].isFull()){

              n.split(i);

              /* Recheck the new key.  Which tree do we descend into? */
              n = n.child[key.lte(n.key[i])?i:++i];

            } else {

              /* We've run out of keys.  Descend down the tree */
              n = n.child[i];

            }

            /* Break out of the loop gracefully */
            i = keymx;
          }
        }
      }

      /* At this point we must insert into this node. */

      i=0; 

      while(n.key[i] != null && key.gt(n.key[i]))
        i++;

      if(n.key[i] != null && key.eq(n.key[i]))
        throw new DuplicateDataException(n.key[i]);

      n.insert(key, i);
    }

    /**
     * Take the largest key and child from i-1 and put it in i.
     */
    public void takefromsmaller(int i)
    {
      /* Compute which child to take. */
      int j = child[i-1].keycount()-1;

      /* move parent key into child i */
      child[i].insert(removeKey(i-1), 0);

      /* move child i-1 key into parent. */
      insert(child[i-1].removeKey(j), i-1);

      /* Move the child from sibling to the other sibling. */
      child[i].insert(child[i-1].removeChild(j+1), 0);
    }

    /* Snag the next child's key and child. */
    public void takefromlarger(int i)
    {
      /* take sibling */
      /* Compute where to add the new child to child[i]. */
      int j = child[i].keycount();

      child[i].insert(removeKey(i), j);

      /* Move key from sibling to parent. */
      insert(child[i+1].removeKey(0), i);

      /* Move child from sibling to sibling. */
      child[i].insert(child[i+1].removeChild(0), j+1);

    }

    /**
     * Repair nodes before descending into them for deletion.
     * More specifically, if a deletion would cause the child node i to
     * violate the rule of having at least keymn-1 keys, then this must
     * increase the node size.
     * Returns 1 if a merge occurs.  This CHANGES the position of the key
     * relative to i 
     */
    public int predelfixup(int i)
    {
      /* If this is not a leaf and may not lose another key.*/
      if( ! isLeaf() && child[i]!=null && child[i].canMerge()){

        /* If there is a child to the left...*/
        if(i>0){
          if(child[i-1].hasExtraKeys()){

            takefromsmaller(i);

          } else {

            merge(i-1);
            return 1;

          }

        } else {

          /* We only arrive here is i = 0 */

          if(child[1].hasExtraKeys()){

            takefromlarger(0);

          } else {

            merge(0);

          }
        }
      }

      return 0;
    }

    /**
     * Delete a key from a sub tree rooted at n.
     * We must consider the subtree incase there are duplicate keys 
     * Note that this method expects that the node it is in does not violate
     * of the BTree constraints and may loose a key if necessary.
     */
    public Key<E> del(Key<? extends Object> key)
    {
      Key<E> tmpkey = null; /* The key we have removed. */
      Node n      = this; /* Searching node and node to remove key from. */

      int i,         /* Simple iterator variable. */
      keyi = -1; /* What index in n to remove. */

      /* Until we find some reason to exit. */
      while ( n != null && keyi == -1 ) {

        for ( i = 0; i < keymx; i++ ) {

          /* Delete from the left (less-than) child. */
          if ( n.key[i] == null || n.key[i].gt(key) ) {

            i -= n.predelfixup(i); /* Fix up the node. */
            n  = n.child[i];       /* Descend into this node. */
            i  = keymx;            /* Break out of for loop. */

            /* The key to remove is found and we gracefully exit the loop. */
          } else if ( n.key[i].eq(key) ) {

            keyi = i;     /* This is the key to remove. */
            i    = keymx; /* Break out of the for loop. */

            /* Del descends into the right (more-than) child. */
          } else if ( i == keymx - 1 ) {

            i    = keymx - n.predelfixup(keymx);
            n    = n.child[i];

          }
        }
      }

      /* Have we failed to find with a key to remove? */
      if ( n == null )
        return null;

      /* If we gets here we have key (keyi) to remove from a node (n). */

      /* Delete the value and put it in tmpkey. */
      /* Deleting a key from a leaf is trivial; Call n.removeKey(keyi). */
      if ( n.isLeaf() ) {

        tmpkey = n.removeKey(keyi);

      } else {

        /* Replace this key with the max of the less-than subtree. */
        if(n.child[keyi].hasExtraKeys()){

          tmpkey      = n.key[keyi];
          n.key[keyi] = n.child[keyi].delmax();

          /* Replace this key w/ the min key from the larger-than subtree. */
        } else if ( n.child[keyi+1].hasExtraKeys() ) {

          tmpkey      = n.key[keyi];
          n.key[keyi] = n.child[keyi+1].delmin();

          /* Merge tree keyi and keyi+1 into keyi and delete from keyi. */
        } else {

          n.merge(keyi);
          tmpkey = n.child[keyi].del(key);

        }
      }

      return tmpkey;
    }

    public void log(String s){ System.err.println(s); }

    /**
     * Select the data object according to the given KeySelection.
     */
    public void select(KeySelection sel, List<E> lst)
    {
      int i = 0;

      for ( ; i < keymx; i++ ) {

        if ( child[i] != null )
          child[i].select(sel, lst);

        if ( key[i]!=null && sel.inSet(key[i]) )
          lst.add(key[i].getData());
      }

      if ( child[i]!=null )
        child[i].select(sel, lst);
    }

    /**
     * Select the Key according to the given KeySelection.
     */
    public void selectKeys(KeySelection sel, List<Key<E>> lst)
    {
      int i = 0;

      for ( ; i < keymx; i++ ) {
        if ( child[i]!=null )
          child[i].selectKeys(sel, lst);

        if ( key[i]!=null && sel.inSet(key[i]) )
          lst.add(key[i]);
      }

      if ( child[i]!=null )
        child[i].selectKeys(sel, lst);
    }

    /**
     * Select all the keys from min to max, inclusively.
     */
    public void selectKeys(List<Key<E>> lst, Key<? extends Object> min, Key<? extends Object> max)
    {
      int i = 0;

      /* For every key in this node. */
      for ( ; i < keymx && key[i] != null; i++ ) {

        if ( key[i].gte(min) ) {

          if ( child[i] != null )
            child[i].selectKeys(lst, min, max);

          if ( key[i].lte(max) )
            lst.add(key[i]);

          else
            return;

        }
      }

      if ( i <= keymx && child[i] != null )
        child[i].selectKeys(lst, min, max);
    }

    /**
     * Select all the date objects with keys from min to max, inclusively.
     */
    public void select(List<E> lst, Key<? extends Object> min, Key<? extends Object> max)
    {
      int i = 0;

      /* For every key in this node. */
      for ( ; i < keymx && key[i] != null; i++ ) {

        if ( key[i].gte(min) ) {

          if ( child[i] != null )
            child[i].select(lst, min, max);

          if ( key[i].lte(max) )
            lst.add(key[i].getData());

          else
            return;

        }
      }

      if ( i <= keymx && child[i] != null )
        child[i].select(lst, min, max);
    }

    public void toKeyList(List<Key<E>> lst)
    {
      int i = 0;

      for(; i<keymx; i++){
        if(child[i]!=null)
          child[i].toKeyList(lst);

        if(key[i]!=null)
          lst.add(key[i]);
      }

      if(child[i]!=null)
        child[i].toKeyList(lst);
    }

    public void toList(List<E> lst)
    {
      int i = 0;

      for(; i<keymx; i++){
        if(child[i]!=null)
          child[i].toList(lst);

        if(key[i]!=null)
          lst.add(key[i].getData());
      }

      if(child[i]!=null)
        child[i].toList(lst);
    }
  }
  /**************** CLOSE INTERNAL CLASS ********************/

  /**
   * Minbr is the minimum branching factor for the tree.  It may not be
   * less than 2.  If it is set to a value below 2, the tree defaults to using
   * 2 as the value.
   */
  public BTree(int minbr)
  {

    if(minbr<2)
      minbr = 2;

    keymx = minbr * 2 - 1;
    keymn = minbr;

  }

  public BTree()
  {
    keymx = 2 * 2 - 1;
    keymn = 2;
  }

  public void delAll()
  {
    root = null;
    size = 0;
  }

  public Key<E> del(Key<? extends Object> key)
  {
    Key<E> tmpkey = null;
    int i;

    if(size==0)
      return null;

    /* Where do we look? */
    i = root.whichsubtree(key);

    /* Did which subtree find the key in the largest subtree. */
    if(i >= keymx || root.key[i] == null){

      i      -= root.predelfixup(i);

      if(root.key[0] == null){

        root   = root.child[0];
        tmpkey = root.del(key);

      } else if(root.child[i]!=null){

        tmpkey  = root.child[i].del(key);

      }

    } else if(key.eq(root.key[i])){

      /* Here, the key is *in* the root node. */

      if(root.isLeaf()){

        tmpkey = root.removeKey(i);

        /* Can we delete from this node w/ trouble? */
      } else if(root.child[i].hasExtraKeys()){

        tmpkey = root.key[i];

        /* Take neighbor child */
        root.key[i] = root.child[i].delmax();

      } else if(root.child[i+1].hasExtraKeys()){

        tmpkey = root.key[i];

        /* Take neighbor child. */
        root.key[i] = root.child[i+1].delmin();

      } else {

        /* merge nodes i and i+1 normally */
        root.merge(i);

        if(root.key[0] == null)
          root = root.child[0];

        /* Remove the child key. */
        tmpkey = root.del(key);

      }

    } else {

      i      -= root.predelfixup(i);

      if(root.key[0] == null){

        root   = root.child[0];
        tmpkey = root.del(key);

      } else if(root.child[i] != null){

        tmpkey  = root.child[i].del(key);

      }

    }

    if(tmpkey != null)
      size--;

    root.chkTree();

    return tmpkey;
  }

  public void add(Key<E> key)
  {
    Node nroot; /* a pointer just for root spliting */

    if(root==null){

      root = new Node();
      root.key[0] = key;
      size = 1;

    } else {


      /* If the root node is full, split it before proceeding */
      /* To split the root, we make a new root and split its only child */
      if(root.isFull()){

        nroot = new Node();

        nroot.child[0] = root;
        root           = nroot;

        root.split(0);
        root.chkTree();
      }

      root.add(key);

      size++;
    }

  }

  public void addUnique(Key<E> key) throws DuplicateDataException
  {
    Node nroot; /* a pointer just for root spliting */

    if(root==null){

      root = new Node();
      root.key[0] = key;
      size = 1;

    } else {


      /* If the root node is full, split it before proceeding */
      /* To split the root, we make a new root and split its only child */
      if(root.isFull()){

        nroot = new Node();

        nroot.child[0] = root;
        root           = nroot;

        root.split(0);
        root.chkTree();
      }

      root.addUnique(key);

      size++;
    }

  }

  public void chkTree()
  {
    root.chkTree();
  }

  public int size(){ return size; }

  public List<Key<E>> selectKeys(KeySelection sel)
  {
    List<Key<E>> lst = new DynamicTable<Key<E>>();
    if(root!=null){
      root.selectKeys(sel, lst);
    }
    return lst;
  }

  /**
   * Make a list of keys.
   */
  public void selectKeys(KeySelection sel, List<Key<E>> lst)
  {
    if(root!=null){
      root.selectKeys(sel, lst);
    }
  }

  /**
   * Make a list of keys that conform to the selection. 
   * Takes order n to complete.
   */
  public List<E> select(KeySelection sel)
  {
    List<E> lst = new DynamicTable<E>();
    if(root!=null){
      root.select(sel, lst);
    }
    return lst;
  }

  /**
   * Make a list of keys.
   */
  public void select(KeySelection sel, List<E> lst)
  {
    if ( root != null )
      root.select(sel, lst);
  }

  public void selectKeys(List<Key<E>> lst, Key<? extends Object> min, Key<? extends Object> max)
  {
    if ( root != null ) {

      /* If min is *NOT* min <= max then switch them. */
      if ( min.lte(max) )
        root.selectKeys(lst, min, max);
      else
        root.selectKeys(lst, max, min);
    }
  }

  public void select(List<E> lst, Key<? extends Object> min, Key<? extends Object> max)
  {
    if ( root != null ) {

      /* If min is *NOT* min <= max then switch them. */
      if ( min.lte(max) )
        root.select(lst, min, max);
      else
        root.select(lst, max, min);
    }
  }

  /**
   * Make a list of keys.
   */
  public List<Key<E>> toKeyList()
  {
    /* We could avoid this if the list is size 0, but returning
     * null in the case of an empty list puts uneeded complexity
     * on the user layers.  If they want to check that the
     * list is of size > 0, they can do that themselves.
     */
    List<Key<E>> lst = new DynamicTable<Key<E>>(size);

    if(root!=null){
      root.toKeyList(lst);
    }

    return lst;
  }

  /**
   * Make a list of keys.
   */
  public void toKeyList(List<Key<E>> lst)
  {
    if(root!=null){
      root.toKeyList(lst);
    }
  }


  /**
   * Make a list of data object.
   */
  public void toList(List<E> lst){
    if(root!=null){
      root.toList(lst);
    }
  }

  /**
   * Make a list of data object.
   */
  public List<E> toList(){
    List<E> lst = new DynamicTable<E>(size);

    if(root!=null){
      root.toList(lst);
    }

    return lst;
  }

  public Key<E> find(Key<? extends Object> k)
  {
    Key<E> result = null;

    if(root!=null)
      result = root.find(k);
    return result;
  }

  public Key<E> max(){ return root==null? null : root.max(); }
  public Key<E> min(){ return root==null? null : root.min(); }

  public void print()
  {
    if ( root == null )
      System.out.println("Tree is empty.");
    else
      root.print(); 
  }

  public State minLocation()
  {
    return ( root == null ) ? null : root.minLocation();
  }

  public State maxLocation()
  {
    return ( root == null ) ? null : root.maxLocation();
  }
}
