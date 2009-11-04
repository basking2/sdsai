/* $Id: ObjectSpace.java 763 2008-08-27 21:52:19Z sam $ */

package org.sdsai.util;

import org.netobj.DataObject;
import org.netobj.ListObject;
import org.netobj.NetObject;
import org.sdsai.List;

/**
 * This is a heirachical collection of named objects.  This class
 * allows the user to add ACL (Access Control Lists) to each object
 * and those permissions are inherited to lower object.  Also,
 * timed locking is supported as well.  The user can 
 * also map object in this object space to instances of Java Objects.
 *
 * <p>Every object is a ListObject with sublists of meta data.  Objects
 * may all have sub objects.  Sub object, if they do not define their own
 * security permissions retain those of their parents.
 *
 * <p><b>Note on Locks</b>
 * <p>Locks do not enforce mutual exclusion in ObjectSpaces. Rather they
 * provide timed record keeping for who has what lock.  Users of ObjectSpace
 * locks should, if they access ObjectSpace with multiple threads,
 * should not modify the data structure during locking operations.
 * 
 */
public class ObjectSpace extends ListObject
{
  protected ListObject acl;
  protected ListObject userAttr;
  protected ListObject subObjs;
  protected ListObject locks;

  /** 
   * Allow infinite accesses.
   */
  public static final int PERM_INFACCESSES = -1;
  
  public static final int PERM_ALLOWED     = 1;
  public static final int PERM_DISALLOWED  = 2;
  public static final int PERM_EXPIRED     = 3;
  public static final int PERM_NOACCESSES  = 4;
  public static final int PERM_UNDEFINED   = 5;

  public ObjectSpace(String name)
  {
    super(name);

    locks    = new ListObject("locks");
    subObjs  = new ListObject("sub objects");
    userAttr = new ListObject("user attributes");
    acl      = new ListObject("ACL");

    add(locks);
    add(subObjs);
    add(userAttr);
    add(acl);
  }

  public void setAttribute(DataObject d)
  {
    userAttr.set(d);
  }

  public void setAttribute(ListObject l)
  { 
    userAttr.set(l);
  }

  public NetObject getAttribute(String s)
  {
    return userAttr.find(s, null);
  }

  public NetObject delAttribute(String s)
  {
    return userAttr.del(s);
  }

  public List<NetObject> lsObjects()
  {
    return subObjs.toList();
  }

  /**
   * Make an object in the object space.
   */
  public ObjectSpace mkObject(Object[] path)
  {
    ObjectSpace l = path.length > 0 ? this:null;

    for(int i=0; i < path.length; i++){
      l = l.mkObject(path[i].toString());
    }
    
    return l;
  }

  /**
   * Make an object in the object space.
   */
  public ObjectSpace mkObject(String name)
  {
    ObjectSpace l = new ObjectSpace(name);
    subObjs.set(l);
    return l;
  }

  public ObjectSpace rmObject(String name)
  {
    return (ObjectSpace) subObjs.del(name);
  }

  public ObjectSpace rmObject(Object[] path)
  {
    if(path.length > 0){
      ObjectSpace ret = this;
      
      /* Finds the parent. */
      for(int i = 0; i < path.length-1; i++){
        
        ret = (ObjectSpace) ret.subObjs.find(path[i].toString(), null);
        
        if(ret == null)
          return null;
        
      }
      
      return (ObjectSpace)ret.del(path[path.length-1].toString());
    }

    return null;
  }

  public ObjectSpace findObject(String s)
  {
    return (ObjectSpace)subObjs.find(s, null);
  }
  
  public ObjectSpace findObject(Object[] path)
  {
    ObjectSpace os = path == null || path.length > 0 ? this : null;

    for(int i=0; i < path.length; i++){
      
      os = (ObjectSpace)os.find(path[i].toString(), null);
      
      if(os == null) 
        break;
      
    }

    return os;
  }

  /**
   * Descend the tree to path.length - 1.
   */
  public ObjectSpace findObjectParent(Object[] path)
  {
    ObjectSpace os = null;

    if(path != null && path.length > 0){
      
      os = this;
      
      for(int i=0; i < path.length-1; i++){
	
	os = os.findObject(path[i].toString());
	
	if(os == null) 
	  break;
      }
    }

    return os;
  }

  /**
   * Return the ListObject that contains other ObjectSpaces.
   * Users should not alter this object.
   */
  public ListObject getObjects()
  {
    return subObjs;
  }

  /**
   * Add a permission that allows or disallows the type of action given.
   * Permissions should be enforced by the manipulator of this object.
   */
  public void setPermission(String id, String action, boolean allow)
  {
    ListObject perm = (ListObject) acl.find(id, null);
    ListObject type = new ListObject(action);

    if(perm == null){
      perm = new ListObject(id);
      acl.add(perm);
    }

    perm.set(type);

    type.add(new DataObject("allow", (allow ? 1 : 0)));
    type.add(new DataObject("accesses", PERM_INFACCESSES));
    type.add(new DataObject("expiration", (long)0));
  }

  /**
   * Remove all permissions from this object.
   * This is handy when the permissions are intended to be inherited.
   * What this does (under the hood) is creates a new, empty ACL, and
   * sets it to this object's ACL.
   */
  public void unsetAllPermissions()
  {
    acl = new ListObject("ACL");

    set(acl);
  }

  /**
   * Remove all permissions for the id.
   */
  public void unsetAllPermissions(String id)
  {
    acl.del(id);
  }

  /**
   * Remove the permission <i>action</i> for the given id.
   */
  public void unsetPermission(String id, String action)
  {
    ListObject l = (ListObject) acl.find(id, null);

    if(l!=null){
      l.del(action);
    }
  }

  /**
   * Almost identical to setPermission(String, String, boolean).
   * @param id - The ID typically corresponding to a user name.
   * @param action - The name of the permission.
   * @param allow - True or false corresponding to if this action is 
   * allowed or not.
   * @param accessCount - The number of times this permission may be
   * accessed. Each check of this permission will decrement this value. 
   * A value of PERM_INFACCESSES indicates no limit of infinite access.
   * @param expiration - The time in milliseconds when this permission
   * record is no longer valid.  An example would be System.currentTimeMillis()
   * + 60000 which will expire the value in 10 minutes. Set this to 0
   * for no expiration.
   */
  public void setPermission(String id, String action, boolean allow,
                            int accessCount, long expiration)
  {
    ListObject perm = (ListObject) acl.find(id, null);
    ListObject type = new ListObject(action);

    if(perm == null){
      perm = new ListObject(id);
      acl.add(perm);
    }

    perm.set(type);

    type.add(new DataObject("allow", (allow ? 1 : 0)));
    type.add(new DataObject("accesses", accessCount));
    type.add(new DataObject("expiration", expiration));
  }

  /**
   * Returns the ACL component of this object.
   * The ACL contains ListObjects named as user IDs. 
   * The ID tables each contain a list of ListObjects which are the types
   * of actions.  The type of action table contains "allow" which is an
   * Int32 DataObject which if non-zero allows the action, "access" which
   * is the number of accesses a user may have before this permission is 
   * destroyed and "expiration" which is an Int64 DataObject which 
   * is the time after which this permission is no longer good.
   *
   * <p>NOTE: The user should only use this method to read the contents of
   * the ACL, not to modify it.  
   */
  public ListObject getACL()
  { 
    return acl;
  }

  /**
   * Check that all permissions in this object and along the given path
   * are set to val.  If a child
   * directory is not set to val, but PERM_UNDEFINED, it is <b>not</b>
   * a failure case. The permission is assumed to inherit from the parent.
   * This is VERY useful for things like checking that a user can 
   * enter all directories in a path to an object.
   * For example, checkPathPermission("bob", "read", somePath, PERM_ALLOWED)
   * will check that bob may read each directory in the path.
   * @param id - the user id.
   * @param action - the action to take
   * @param path - the path to check
   * @param val - what the permission should be set to. PERM_ALLOWED or
   * PERM_DISALLOWED (typically).  Whatever should be returned from 
   * checkPermission.
   * @return False if the parent object does not have the permission named
   * "action" set to "val", any of the subobjects have the permission named
   * "action" not set to "val" or have it undefined, or any object in the
   * path does not exist.
   */
  public boolean checkPathPermission(String id, 
				     String action, 
				     Object[] path, 
				     int val)
  {
    ObjectSpace o = this;

    /* Check that the use can perform this operation on this object. */
    int p = checkPermission(id, action);

    /* If p == val then we have a chance to make this true! */
    if(p == val){
      
      /* If there is a path, keep checking! We could still fail! */
      if(path != null && path.length > 0){
	
        for(int i=0; i<path.length; i++){
          o = o.findObject(path[i].toString());
          /* The permission doesn't check out if the objects aren't defined. */
          if( o == null)
            return false;

          p = o.checkPermission(id, action);

          /* Bummer, we failed. */
          if(p != val && p != PERM_UNDEFINED)
            return false;
        }
      }

      /* If we go through the WHOLE for loop w/o failing, we win! */
      return true;
    }

    return false;
  }

  /**
   * Check that a permission is set along an entire path to an object's parent.
   * This is the same as checkPathPermission except the last element
   * in path is not evaluated. Conceptually only the path to the parent is
   * checked.
   */
  public boolean checkParentPathPermission(String id, 
					   String action, 
					   Object[] path, 
					   int val)
  {
    ObjectSpace o = this;

    int p = o.checkPermission(id, action);

    /* If p == val then we have a chance to make this true! */
    if(p == val){
      
      /* If there is a path; Keep checking! We could still fail! */
      if(path != null && path.length > 0){
	
        for(int i=0; i<path.length-1; i++){
          o = o.findObject(path[i].toString());

          /* If the object is not defined we return where we are. */
          if(o == null){
            if(p == val)
              return true;
            else
              return false;
          }

          p = o.checkPermission(id, action);

          /* Bummer, we failed. */
          if(p != val && p != PERM_UNDEFINED)
            return false;
        }
      }

      /* If we go through the WHOLE for loop w/o failing, we win! */
      return true;
    }

    return false;
  }

  /**
   * Check the permission inheriting as we go along the path.
   */
  public int checkPermission(String id, String action, Object[] path)
  {
    ObjectSpace o = this;

    int ret       = checkPermission(id, action);

    for(int i=0; i < path.length; i++){

      /* Find and object. */
      o = o.findObject(path[i].toString());

      if(o != null){

        int tmpperm = o.checkPermission(id, action);

        if(tmpperm != PERM_UNDEFINED)
          ret = tmpperm;

      } else {

        break;

      }

    }

    return ret;
  }

  public int checkPermission(String id, String action)
  {
    ListObject perm = (ListObject)acl.find(id, null);

    int ret;
    
    /* Do we have the object of the permission? */
    if(perm == null){

      ret = PERM_UNDEFINED;

    } else {

      ListObject type  = (ListObject) perm.find(action, null);

      /* Does this object even have the action defined? */
      if(type == null){

        ret = PERM_UNDEFINED;

      } else {

        int  allow       = ((DataObject)type.find("allow", null)).toInt32();
        long expiration  = ((DataObject)type.find("expiration", null)).toInt64();
        int  accesses    = ((DataObject)type.find("accesses", null)).toInt32();

        /* If this has expired it takes priority. */
        if(expiration > 0 && expiration < System.currentTimeMillis()){
          
          ret = PERM_EXPIRED;
          
          /* If we have used up all the access of this permission. */
        } else if(accesses == 0){
          
          ret = PERM_NOACCESSES;

          /* Delete this action so type==null is true next time around. */
          perm.del(action);
          
          /* If there are some accesses left. */
        } else if(accesses > 0){
          
          type.set(new DataObject("accesses", accesses - 1));
	  
          ret = PERM_ALLOWED;
          
        } else if(allow == PERM_INFACCESSES){
          
          ret = PERM_ALLOWED;

        } else {

          ret = PERM_DISALLOWED;

        }
      }
    }

    return ret;
  }

  /**
   * Try to add a lock and fail if a lock of <i>type</i> already
   * exists.  As in all cases, access to this object must be serialized
   * by the user.  If this is called on a lock that is already held by
   * <i>id</i>, the lock is replaced with a new one with the new timeout.
   * @param id - a way to identify who has the lock. This must match to unlock.
   * @param type - the type of the lock. Read Only, Write Only, What Have You.
   * @param timeout - how many milliseconds until this lock is invalid.
   * @return The time remaining in the current lock or zero on success.
   */
  public long tryLock(String id, String type, long timeout)
  {
    long       timeLeft = 0;
    ListObject lock     = (ListObject) locks.find(type, null);

    timeout += System.currentTimeMillis();

    /* If there is no lock... */
    if(lock == null){

      lock = new ListObject(type);

      lock.add(new DataObject("id", id));
      lock.add(new DataObject("timeout", timeout));

      locks.add(lock);
      
      timeLeft = 0;

      /* If there is a lock but it has expired. */
    } else if(
              /* Is this timed out? */
              ((DataObject)lock.find("timeout", null)).toInt64() < 
              System.currentTimeMillis() ||
              
              /* or is do we own this? */
              id.equals(((DataObject)lock.find("id", null)).toString())){
      
      lock = new ListObject(type);

      lock.add(new DataObject("id", id));
      lock.add(new DataObject("timeout", timeout));

      locks.set(lock);

      timeLeft = 0;
      
      /* In any other case, this is a failed lock. */
    } else {

      timeLeft = ((DataObject)lock.find("timeout", null)).toInt64();

    }

    return timeLeft;
  }

  /**
   * This simply calls tryLock.
   */
  public long renewLock(String id, String type, long timeout)
  {
    return tryLock(id, type, timeout);
  }

  public String getLocker(String typeName)
  {
    ListObject type = (ListObject) locks.find(typeName, null);
    String     id   = null;

    if(type != null){
      
      DataObject d = (DataObject) type.find("id", null);

      if(d != null){

        id = d.toString();

      }
    }

    return id;
  }

  /**
   * The user should serialize access to this data object and call this
   * in the critical section before doing operations.  
   * @return True if id has lock of type <i>type</i>.
   */
  public boolean hasLock(String id, String type)
  {
    String id2 = getLocker(type);

    return (id2 != null && id.equals(id2));
  }

  /**
   * If we own the lock, unlock it. Otherwise this does nothing. 
   */
  public void unlock(String id, String type)
  {
    String id2 = getLocker(type);

    if(id2 != null && id.equals(id2)){
      locks.del(type);
      
      synchronized(this){
	notifyAll();
      }
    }

  }

  /**
   * Delete the lock effectivly unlocking this object.  This is obviously
   * dangerous.
   */
  public void forceUnlock(String type)
  {
    locks.del(type);
  }

  /**
   * Since this method is most likely going to be executed in a thread besides
   * one that is manipulating the ObjectSpace it is important to remember
   * that the user must provide serialized access to this object.
   * While redundant to provide locks to call locking functions,
   * it is necessary because this object is designed to function best in
   * a single thread.
   */
  public void lock(String id, String type, long timeout)
  {
    long waittime = tryLock(id, type, timeout);

    while(waittime > 0){

      try { 

        synchronized(this){ this.wait(waittime); } 

      } catch(InterruptedException e){
        
      }

      waittime = tryLock(id, type, timeout);

    }
  }

  public String toString()
  {
    return name;
  }
}
