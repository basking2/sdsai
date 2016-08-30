/* $Id: UFSet.java 312 2006-07-03 22:20:47Z sam $ */

package com.github.basking2.sdsai;


/**
 * This is a union-find set.  This a quick algorithm to put things
 * into sets.  The running time is, nlog*(n), amortized.  In the worst case,
 * though, we could have a O(log(n)) call to find.
 * The intention is for other classes to extend this set so as to 
 * make use of its methods.
 */
public class UFSet 
{

  int   rank;
  UFSet parent;

  public UFSet() { rank=0; parent=this; }

  /**
   * This method resets the set in wich this object, and all sub objects
   * reside to being named after this object.  This is typically done to
   * all UFSet objects at the same time.  Union/Find does not 
   * specify removal from a set.  
   */
  public void reset(){ parent = this; }

  /**
   * Returns true if the calling instance and m are members of the
   * same set.
   */
  public boolean member(UFSet m){ return find()==m.find(); }

  /**
   * Taken from the name of the well known algorithm.
   * This returns the "name" of the set to which the calling
   * object belongs.  That is, the representative element is returned.
   */
  public UFSet find(){
    UFSet obj=this;
    /* Find the representative element */
    while(obj != obj.parent)
      obj=obj.parent;

    UFSet tmp=this;
    while(tmp!=tmp.parent){
      UFSet tmp2=tmp;
      tmp=tmp.parent;
      tmp2.parent=obj;
    }

    return obj;
  }

  /*
   * This is simply a recursive version of find().  It does not ever need
   * to be used, but was too pretty not to include for developers to look at.
   * Note that it is not tail recursive and is MUCH less efficient than
   * simply using find as far as work the computer must do.
   */
  /*
  public UFSet rfind(){
    if(this!=parent)
      parent=parent.find();
    return parent;
  }
  */

  /**
   * Union two sets of object together in constant time.
   * The new set "name" is returned.
   */
  public UFSet union(UFSet u){
    if(rank<u.rank){
      parent=u;
      return u;
    }

    u.parent=this;           /* The parent is this, regardless */

    if(rank==u.rank) rank++; /* If the ranks are equal, we increase ours */

    return this;
  }
}
