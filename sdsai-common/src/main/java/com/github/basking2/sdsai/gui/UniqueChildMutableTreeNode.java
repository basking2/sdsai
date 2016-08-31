package com.github.basking2.sdsai.gui;

import com.github.basking2.sdsai.Key;
import com.github.basking2.sdsai.RedBlackTree;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 * This class extends DefaultMutableTreeNode but uses an
 * RedBlackTree to guarantee that no child may be added
 * whose toString method returns a string that has already been added.
 * That is to say, no two children may be "child1".
 * @author sam
 */
public class UniqueChildMutableTreeNode extends DefaultMutableTreeNode 
{

  private static final long serialVersionUID = 1L;

  private RedBlackTree<UniqueChildMutableTreeNode> index = null;
  
  private Key<UniqueChildMutableTreeNode> key = null;
  
  public UniqueChildMutableTreeNode(Object s) { super(s, true); }
  
  public RedBlackTree<UniqueChildMutableTreeNode> getIndex()
  {
    if ( index == null ) {
      index = new RedBlackTree<UniqueChildMutableTreeNode>();
    }
    
    return index;
  }
  
  public Key<UniqueChildMutableTreeNode> getKey()
  {
    if ( key == null ) {
      key = new Key<UniqueChildMutableTreeNode>(getUserObject().toString(), this);
    }
    
    return key;
  }
  
  public void add(UniqueChildMutableTreeNode n)
  {
    Key<UniqueChildMutableTreeNode> k = getIndex().del(n.getKey());
    
    /* If success removing from index, then remove from this node. */
    if ( k != null )
      remove(k.getData());

    getIndex().add(n.getKey());
    super.add(n);
  }
  
  public void remove(UniqueChildMutableTreeNode n)
  {
    getIndex().del(n.getKey());
    super.remove(n);
  }
}
