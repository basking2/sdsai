/* $Id: DataShare.java 752 2008-08-06 21:03:46Z sam $ */

package org.sdsai.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.netobj.DataObject;
import org.netobj.NetObject;
import org.sdsai.DynamicTable;
import org.sdsai.List;

/**
 * A DataShare contains an ObjectSpace. That object space
 * maps to a location on the file system (typically a directory)
 * and all the files at that location are shared.  The ObjectSpace
 * may also contain ObjectSpaces and so may contain other mappings.
 *<p><b>Permission Notes</b>
 *<p>These are a few notes on methods that take and ID as their first 
 * argument and the permissions that are checked.
 *<ul> All of these need PERM_ENTER to operate.
 *<li> append - PERM_APPEND
 *<li> del - PERM_DELETE
 *<li> ls - PERM_READ
 *<li> write - PERM_WRITE
 *<li> openInputStream - PERM_READ
 *<li> openOutputStream - PERM_WRITE
 *<li> tryLock, holdsLock - Needs lock permissions, PERM_LOCK.
 *<li> unlock - Enter permissions. Make unlocking easy for the owner.
 *<li> unmap, map - Admin Permissions
 *<li> setPermission, unsetPermission, unsetAllPermissions - Admin Permission on the object.
 *</ul>
 */
public class DataShare 
{
  /* How long a lock on an object lasts (barring the user unlocking it). */
  public static final long LOCK_TIME = 180000;

  /**
   * Allow a user to enter an object. This allows a user to enter
   * a path of objects without being able to list the files in the
   * directory.   This is required for a user to "touch" any part of an object.
   * A user must be able to enter an object to read it, read sub objects,
   * lock it, create subobjects, apend, modify, delete, etc, etc, etc.
   * <p>The reason this even exists is to be able to 1) lock out users from
   * all permissions at one place and 2) prevents inference attacks for
   * users with no permissions.
   */
  public static final String PERM_ENTER        = "enter";

  public static final String PERM_READ         = "read";

  /**
   * Read but for directories.
   */
  public static final String PERM_LIST         = "list";
  public static final String PERM_WRITE        = "write";
  public static final String PERM_LOCK         = "lock";
  public static final String PERM_CREATE       = "create";
  public static final String PERM_APPEND       = "append";
  public static final String PERM_MODIFY       = "modify";
  public static final String PERM_DELETE       = "delete";

  /**
   * Execute special types of ObjectSpaces added to DataShares.
   */
  public static final String PERM_EXECUTE      = "execute";

  public static final String PERM_FORCE_UNLOCK = "force unlock";

  /**
   * Ids with this permission may read and alter the premissions.
   * This depends on the PERM_ENTER permission. 
   */
  public static final String PERM_ADMIN        = "admin";

  /**
   * This is the guts of the DataShare.
   * We do not want the user to touch this directly so we do not
   * extend this.
   */
  protected ObjectSpace share;

  /**
   * Create a DataShare named <i>name</i> and which points to <i>dirName</i>.
   * Note that directories and files are accessed as needed.  If <i>dirName</i>
   * does not exist or is not readable <b>or is a relative path</b> the
   * results will not be known until access to the local files
   * is attempted.  Do note that relative paths can cause trouble
   * if the program this is running is ever changes directories.
   * Absolute paths are much preffered.
   */
  public DataShare(String dirName)
  {
    share    = new ObjectSpace(dirName);

    share.setAttribute(new DataObject("Local File", dirName));
  }

  /**
   * @param userid - a string identifying who is talking to this object.
   * @param action - this should be one the PERM_* available from this class.
   * @param allow - let this happen or not.
   */
  public void setPermission(String userid, String action, boolean allow)
  {
    share.setPermission(userid, action, allow);
  }

  public void setPermission(String id, String userid, 
			    String action, boolean allow, Object[] path)
    throws DataShareSecurityException
  {
    /* Can id touch the object? */
    enterCheck(id, path);

    ObjectSpace os = share;

    /* if there is a path, dig out the object we want to modify. */
    if(path != null && path.length > 0){
      os = share.findObject(path);
    }

    /* Check if the object exists and we can administer it. */
    if(os == null ||
       os.checkPermission(id, PERM_ADMIN) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("ID "+id+" does not have permission to administer this object.  Does the object exist?", PERM_ADMIN);
    
    os.setPermission(userid, action, allow);
  }


  /**
   * Almost identical to setPermission(String, String, boolean).
   * @param userid - The ID typically corosponding to a user name.
   * @param action - The name of the permission.
   * @param allow - True or false corrosponding to if this action is 
   * allowed or not.
   * @param accesses - The number of times this permission may be
   * accessed. Each check of this permission will decrement this value.
   * @param expiration - The time in milliseconds when this permission
   * record is nolonger valid.  An examle would be System.currentTimeMillis()
   * + 60000 which will expire the value in 10 minutes. Set this to 0
   * for no expiration.
   */
  public void setPermission(String userid, String action, boolean allow, 
                            int accesses, long expiration)
  {
    share.setPermission(userid, action, allow, accesses, expiration);
  }

  
  public void setPermission(String id, String userid, String action, 
			    boolean allow, int accesses, long expiration,
			    Object[] path)
    throws DataShareSecurityException
  {
    /* Can id touch the object? */
    enterCheck(id, path);

    ObjectSpace os = share;

    /* if there is a path, dig out the object we want to modify. */
    if(path != null && path.length > 0){
      os = share.findObject(path);
    }

    /* Check if the object exists and we can administer it. */
    if(os == null ||
       os.checkPermission(id, PERM_ADMIN) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("ID "+id+" does not have permission to administer this object.  Does the object exist?", PERM_ADMIN);
    
    os.setPermission(userid, action, allow, accesses, expiration);
  }
  
  public void unsetPermission(String userid, String action)
  {
    share.unsetPermission(userid, action);
  }

  public void unsetPermission(String id, String userid, 
			      String action, Object[] path)
    throws DataShareSecurityException
  {
    /* Can id touch the object? */
    enterCheck(id, path);

    ObjectSpace os = share;

    /* if there is a path, dig out the object we want to modify. */
    if(path != null && path.length > 0){
      os = share.findObject(path);
    }

    /* Check if the object exists and we can administer it. */
    if(os == null ||
       os.checkPermission(id, PERM_ADMIN) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("ID "+id+" does not have permission to administer this object.  Does the object exist?", PERM_ADMIN);
    
    os.unsetPermission(userid, action);
  }

  public void unsetAllPermissions(String userid)
  {
    share.unsetAllPermissions(userid);
  }

  public void unsetAllPermissions(String id, String userid, 
				  String action, Object[] path)
    throws DataShareSecurityException
  {
    /* Can id touch the object? */
    enterCheck(id, path);

    ObjectSpace os = share;

    /* if there is a path, dig out the object we want to modify. */
    if(path != null && path.length > 0){
      os = share.findObject(path);
    }

    /* Check if the object exists and we can administer it. */
    if(os == null ||
       os.checkPermission(id, PERM_ADMIN) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("ID "+id+" does not have permission to administer this object.  Does the object exist?", PERM_ADMIN);
    
    os.unsetAllPermissions(userid);
  }

  /**
   * Return the local File object represented by a string in this object.
   */
  public String getLocalFile()
  {
    return ((DataObject)share.getAttribute("Local File")).toString();
  }

  /**
   * List the contents of an object space element.
   */
  private List<String> objectSpaceToList(ObjectSpace os, String id)
    throws DataShareSecurityException
  {
    /* Check permissions. */
    if(os == null)
      throw new DataShareSecurityException("ID "+id+" does not have the correct permissions to list the root index of DataShare \"[null]\".", PERM_LIST);      
    else if(os.checkPermission(id, PERM_LIST) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("ID "+id+" does not have the correct permissions to list the root index of DataShare \"" + os.getName()+"\".", PERM_LIST);

    List<String> list = new DynamicTable<String>();
    
    List<NetObject> los  = os.lsObjects();
    
    /* Add in the other objects. */
    for(int j=0; j < los.size(); j++)
      list.add((los.get(j)).toString());
    
    String fileName = ((DataObject)os.getAttribute("Local File")).toString();
    
    File file = new File(fileName);
    
    /* Now add in the directory entries. */
    if(file.isDirectory()){
      String[] files = file.list();
      
      for(int j=0; j < files.length; j++)
	list.add(files[j]);
      
    } else {
      
      list.add(fileName);
      
    }
    
    return list;
    
  }

  /**
   * Given a prefix, list the contents of what the list holds. Null is returned
   * if there is no object to list. If the object is a file, the name of the
   * file is returned as the only entry in the list.  Pass in an empty list
   * or null to list the objects in the root object.
   * @param path - a list of objects that denote a path.
   * @param id - The ID by whose authority we operate.
   * @throws DataShareSecurityException when an object cannot be entered or
   * read.
   */
  public List<String> ls(String id, Object[] path) throws DataShareSecurityException
  {

    enterCheck(id, path);

    ObjectSpace os = share;

    if(path != null && path.length > 0){

      os = share.findObject(path);

    }

    List<String> resultList;

    if(os == null){
      String p = toLocalPath(path);

      File f = new File(p);

      resultList = new DynamicTable<String>();

      if(f.isDirectory()){
        String[] l = f.list();

        for(int i=0; i<l.length; i++)
          resultList.add(l[i]);
      }

    } else {

      /* objectSpaceToList is very smart and does all our checking. */
      resultList = objectSpaceToList(os, id);

    }

    return resultList;

  }


  /**
   * Do a permission check. Otherwise this is just like the other map.
   */
  public void map(String id, String name, String localFile, Object[] path)
    throws DataShareSecurityException
  {
    enterCheck(id, path);

    if(share.checkPathPermission(id, PERM_ADMIN, path, ObjectSpace.PERM_ALLOWED)){
      map(name, localFile, path);
    }
  }

  public void unmap(String id, Object[] path)
    throws DataShareSecurityException
  {
    enterCheck(id, path);

    if(share.checkPathPermission(id, PERM_ADMIN, path, ObjectSpace.PERM_ALLOWED)){
      unmap(path);
    }
  }

  /**
   * Create or alter a mapping within this datashare. 
   * map("/tmp", new Object[]{ "tmp" } ) will map "tmp" to the local file
   * "/tmp".
   * If the sub-mapping does not exist, it is mapped to the 
   * parent object's location.
   * @param name - if name is null, regardless of path, the mapping of this
   * DataShare's root element is remapped.
   * @param localFile - the target file to map an object to.
   * @param path - this may be an empty list or null if the mapping is to 
   * appear in the root of the share.
   */
  public void map(String name, String localFile, Object[] path)
  {
    ObjectSpace o = share;

    if(name == null){

      share.setAttribute(new DataObject("Local File", localFile));

    } else {
      
      if(path != null)
	for(int i=0; i < path.length; i++){
	  
	  ObjectSpace prevo = o;
	  
	  o = o.findObject(path[i].toString());
	  
	  /* This if() creates a level in the path if it does not exist. */
	  if(o == null){
	    
	    /* If there is no child, make one in the parent. */
	    o = prevo.mkObject(path[i].toString());
	    
	    /**
	     * Point the child where the parent points.
	     * This seems to be the choice of least "bad" impact. 
	     */
	    o.setAttribute(new DataObject("Local File", ((DataObject)prevo.getAttribute("Local File")).toString()));
	    
	  }
	}
      
      ObjectSpace tmpo = o.findObject(name);
      
      if(tmpo == null)
	tmpo = o.mkObject(name);
      
      tmpo.setAttribute(new DataObject("Local File", localFile));

    }
  }

  public void unmap(Object[] path)
  {
    share.rmObject(path);
  }

  /**
   * Take a path and convert it to the path to the local file.
   * The user can take the output of ls(List) and use it as the input here.
   * Note that a file does not need to exist to convert the path. 
   */
  public String toLocalPath(List<String> path)
  {
    String      pathString = getLocalFile();

    ObjectSpace os         = share;

    for(int i = 0; i < path.size(); i++){
      
      /* Can we find a lower level object that matches this path? */
      os = os.findObject(path.get(i).toString());

      if(os == null){

        /* There is no object by this name, so just append the rest of
         * the path to the pathString and exit. */
        for(; i < path.size(); i++)
          pathString += (File.separator + path.get(i).toString());

      } else {

        pathString = ((DataObject)share.getAttribute("Local File")).toString();

      }      
    }

    return pathString;
  }

  public String toLocalPath(Object[] path)
  {
    String      pathString = getLocalFile();

    ObjectSpace os         = share;

    for(int i = 0; i < path.length; i++){
      
      /* Can we find a lower level object that matches this path? */
      os = os.findObject(path[i].toString());

      if(os == null){

        /* There is no object by this name, so just append the rest of
         * the path to the pathString and exit. */
        for(; i < path.length; i++)
          pathString += (File.separator + path[i].toString());

      } else {
	
        pathString = ((DataObject)share.getAttribute("Local File")).toString();

      }      
    }

    return pathString;
  }

  /**
   * This is a check necessary for almost EVERY operation.
   * This is almost a macro.
   * Note that we do this check separately so that timing attackes
   * are more difficult for users that have no permissions on an object.
   */
  protected void enterCheck(String id, Object[] path) 
    throws DataShareSecurityException
  {
    if(!share.checkParentPathPermission(id, PERM_ENTER, path, ObjectSpace.PERM_ALLOWED))
      throw new DataShareSecurityException("Id "+id+" cannot enter object. Check that the object exists and that id "+id+" has the proper permissions.", PERM_ENTER);
    
  }
  
  /**
   * This attempts to lock mapping.  During design it was cosidered
   * if it would be worth while to lock names under the mapping
   * but it was decided that file locking was rare enough so that
   * making maping to files that would be locked was reasonable requirment.
   * This also prevents users making arbitrary locks in spaces where they
   * should not be able to find or creat objects.  This keeps the design
   * more safe.
   */
  public long tryLock(String id, String type, Object[] path)
    throws DataShareSecurityException
  {
    ObjectSpace o = share;

    enterCheck(id, path);
    
    if(!share.checkPathPermission(id, PERM_LOCK, path, ObjectSpace.PERM_ALLOWED))
      throw new DataShareSecurityException("Cannot lock object. Check that it exists and that you have permission to lock the object.", PERM_LOCK);
    
    if(path != null && path.length > 0)
	
      /* get the object. */
      o = share.findObject(path);
    
    /* Try to lock it! */
    return  o.tryLock(id, type, LOCK_TIME);
  }


  public void unlock(String id, String type, Object[] path)
    throws DataShareSecurityException
  {
    ObjectSpace o = share;

    enterCheck(id, path);
    
    if(path != null && path.length > 0)
      /* get the object. */
      o = share.findObject(path);
    
    /* Try to lock it! */
    o.unlock(id, type);
  }

  /**
   * @param id - id of who is asking. They need read prividges and 
   * enter privildges.
   * @param type - the type of lock.
   * @param path - path to the object.
   */
  public String getLocker(String id, String type, Object[] path)
    throws DataShareSecurityException
  {
    enterCheck(id, path);
    
    ObjectSpace o = (path != null && path.length > 0)? 
      share.findObject(path) : share;

    if(o.checkPermission(id, PERM_READ, path) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("Cannot read locker as user id "+id+" does not have read privildges.", PERM_READ);
      
    
    return  o.getLocker(type);

  }

  /**
   * Unlike getLocker, this only requires entr and lock priviledges.
   * @param id - id of who is asking. They need read prividges and 
   * enter privildges.
   * @param type - the type of lock.
   * @param path - path to the object.
   */
  public boolean holdsLock(String id, String type, Object[] path)
    throws DataShareSecurityException
  {
    enterCheck(id, path);

    ObjectSpace o = (path != null && path.length > 0)? 
      share.findObject(path) : share;

    if(o.checkPermission(id, PERM_LOCK) != ObjectSpace.PERM_ALLOWED)
      throw new DataShareSecurityException("Cannot check lock on object. Check that it exists and you have read privilidges.", PERM_LOCK);
    
    String s = o.getLocker(type);

    /* Try to lock it! */
    return (s != null && id.equals(s));
  }

  public FileInputStream openInputStream(String id, Object[]path)
    throws DataShareSecurityException, FileNotFoundException
  {
    enterCheck(id, path);

    ObjectSpace os = (path != null && path.length > 0)?
      share.findObject(path) : share;

    FileInputStream file = null;

    if(ObjectSpace.PERM_ALLOWED == os.checkPermission(id, PERM_READ, path))
      file = new FileInputStream(toLocalPath(path));

    return file;
  }

  public FileOutputStream openOutputStream(String id, Object[] path)
    throws DataShareSecurityException, FileNotFoundException, IOException
  {
    enterCheck(id, path);

    File file = new File(toLocalPath(path));
    FileOutputStream fos = null;

    /* Check if the file exists. If it does, no problem, if not, can we 
     * create it? */
    if(! file.exists()){
      if(share.checkParentPathPermission(id, PERM_CREATE, path, ObjectSpace.PERM_ALLOWED)){
	
	file.createNewFile();

      } else {

	throw new DataShareSecurityException("ID "+id+" cannot create file "+toLocalPath(path), PERM_CREATE);

      }
    }
    
    if(share.checkParentPathPermission(id, PERM_WRITE, path, ObjectSpace.PERM_ALLOWED))
      fos = new FileOutputStream(file);
    else
      throw new DataShareSecurityException("ID "+id+" cannot write to file "+
					   toLocalPath(path), PERM_WRITE);

    return fos;
  }

  public void append(String id, Object[] path, byte[] b)
    throws DataShareSecurityException, FileNotFoundException, IOException
  {
    append(id, path, b, 0, b.length);
  }

  public void append(String id, Object[] path, byte[] b, int off, int len)
    throws DataShareSecurityException, FileNotFoundException, IOException
  {
    enterCheck(id, path);

    File file = new File(toLocalPath(path));
    FileOutputStream fos = null;

    /* Check if the file exists. If it does, no problem, if not, can we 
     * create it? */
    if(! file.exists()){
      if(share.checkParentPathPermission(id, PERM_CREATE, 
					 path, ObjectSpace.PERM_ALLOWED)){

	file.createNewFile();

      } else {

	throw new DataShareSecurityException("ID "+id+" cannot create file: "+toLocalPath(path), PERM_CREATE);

      }
    }
    
    if(share.checkParentPathPermission(id, PERM_APPEND, path, ObjectSpace.PERM_ALLOWED))
      fos = new FileOutputStream(file, true);
    else 
      throw new DataShareSecurityException("ID "+id+" cannot append to file "+toLocalPath(path), PERM_APPEND);

    fos.write(b, off, len);
  }

  public void del(String id, Object[] path)
    throws DataShareSecurityException
  {
    enterCheck(id, path);

    ObjectSpace os = (path != null && path.length > 0)?
      share.findObject(path) : share;
    
    File f = null;
    
    if(ObjectSpace.PERM_ALLOWED == os.checkPermission(id, PERM_DELETE, path))
      f = new File(toLocalPath(path));
    else
      throw new DataShareSecurityException("ID "+id+" does not have permission to delete.", PERM_DELETE);
    
    f.delete();
  }

  /**
   * Return the ObjectSpace inside the DataShare.  This is useful
   * for manipulating permissions that the DataShare may not
   * know about or other low-level functions.  This can be dangerous
   * so use caution when editing the ObjectSpace that the DataShare is using.
   */
  public ObjectSpace getObjectSpace()
  {
    return share;
  }
}
