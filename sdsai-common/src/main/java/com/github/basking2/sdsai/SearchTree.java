/* $Id: SearchTree.java 313 2006-07-10 02:26:03Z sam $ */

package com.github.basking2.sdsai;

import java.util.Stack;

/**
 * This class implements a standard search three.  
 * We add abstraction for n-branching.
 * Because of this, this particular search tree operates slightly 
 * differently than others.  They key at a node may be the max key or
 * any other value.  No direction is implied by it, rather, each
 * node contains a max value which indicates what the maximum
 * value in that tree is.
 */
public class SearchTree {
  SearchTree child;
  SearchTree sibling;
  double     max;  /* copy of the maximum value in children */
  EObject    data;
  int        branches=2;

  public SearchTree(EObject o, int i){branches=i; data=o; max=o.getKey(); }
  public SearchTree(int i){branches=i; }
  public SearchTree(EObject o){ data=o; max=o.getKey(); }

  public boolean empty(){return (child==null && sibling==null && data ==null);}
  
  /** DEBUG Stuff **/

  public void print(){ print("> "); }
  public void print(String s)
  {
    if(empty()) return;

    System.out.println(s+data.getKey() + " : " +max);

    if(child!=null)
      child.print(s+"|  ");
    if(sibling!=null)
      sibling.print(s);
  }

  /**
   * Remove the object with key k.
   */
  public EObject del(double k){
    SearchTree prev   = null;
    Stack<SearchTree> update = new Stack<SearchTree>();
    SearchTree s      = this;

    while(s!=null){

      /** SHOULD WE DO OUR DELETE OPERATION NOW? **/
      if(s.data!=null && s.data.getKey()==k){
        EObject o = s.data;
        
        /* Do deleting */
        if(prev==null && s.child==null){ /* empty tree */
          data = null;
        } else {
          
          while(s.child!=null){ /* decend to the smallest child */
            s.data    = s.child.data;    /* rearrange values as we go */
            
            /* retrieve the last sibling */
            SearchTree tmp = s.child;
            while(tmp.sibling!=null) tmp = tmp.sibling;
            /* Was the vacating value the largest value in the tree? */
            if(s.max<tmp.max) s.max=tmp.max;
            
            prev      = s;
            s         = s.child;
          }

          if(prev.child==s){
            prev.child = s.sibling;
            if(prev.child==null)
              prev.max = prev.data.getKey();
          } else
            prev.sibling = s.sibling;

        }

        /** Should we update any max values? **/
        if(!update.empty()){
          double newmax = ((SearchTree)update.peek()).max();
          
          while(!update.empty())
            ((SearchTree)update.pop()).max = newmax;
        }
        
        return o;
      }
      
      prev = s;
      
      if(s.max<k)
        s = s.sibling;
      else {
        if(s.max==k) update.push(s);
        s = s.child;
      }
    }
    
    return null;
  }

  public double max(){
    SearchTree s = this.child;
    double max   = data.getKey();
    
    while(s!=null){
      while(s.sibling!=null)
        s = s.sibling;
      max = s.max;
      s   = s.child;
    }

    return max;
  }

  public double min(){
    SearchTree s = this;
    while(s.child!=null)
      s=s.child;
    return s.max;
  }

  /**
   * Find or member, what ever you would like to call it.
   */
  public EObject find(double k){
    SearchTree s=this;
    while(s!=null){

      if(s.data.getKey()==k) /* did we find it? */
        return s.data;
      
      s = s.max >= k? s.child: s.sibling;

    }
    
    return null;
  }
  
  public void add(EObject o)
  {
    /* the tree is empty */
    if(data==null){
      data = o;
      max  = o.getKey();
      return; /* a HUGE else statement is too ugly */
    }
    
    /* the tree is childless */
    if(child==null){
      if(max < o.getKey())
        max = o.getKey();
      child = new SearchTree(o, branches);
      return;
    }

    if(max < o.getKey())
      max=o.getKey();

    SearchTree s = this;
    SearchTree c = this.child;
    int cnum=0;

    /* Walk around till we find an empty (null) spot to put data */
    while(c!=null){

      s = c;

      if(o.getKey() > c.max && cnum < branches-1){ /* can I go right? */
        c = c.sibling;
        cnum++;
      } else {                 /* I must step down */
        if(c.max < o.getKey()) /* Since we must go here, check the max. */
          c.max = o.getKey();  /* ... and change it if we must. */
        c    = c.child;
        cnum = 0;
      }
    }
    
    /** When we get here we put the new node either as a child or sibling. */
    if(o.getKey() > s.max)
      s.sibling = new SearchTree(o, branches);
    else 
      s.child = new SearchTree(o, branches);

    /***** STRICT RULES USED FOR DEBUG AND VALIDATION *****
    if(o.getKey() > s.max && cnum < branches && s.sibling == null)
      s.sibling = new SearchTree(o, branches);
    else if(s.child == null && o.getKey() <= s.max)
      s.child = new SearchTree(o, branches);
    else
      System.out.println("***ERROR***"+ o.getKey() +
                         " vs. "+ s.max + " and " + s.data.getKey());
    */
  }
}
