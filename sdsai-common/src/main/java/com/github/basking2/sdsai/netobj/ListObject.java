/* $Id: ListObject.java 670 2008-05-02 20:42:07Z sbaskin $ */

package com.github.basking2.sdsai.netobj;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.basking2.sdsai.Key;
import com.github.basking2.sdsai.ListVisitor;
import com.github.basking2.sdsai.RedBlackTree;

/**
 * A simple list. While this implementation sorts the list the receiver
 * should never assume any particular order to the list.
 */
@Deprecated()
public class ListObject extends NetObject implements Iterable<NetObject> {

    /**
     * A list of things that are NetObjects.
     */
    protected RedBlackTree<NetObject> list;

    public ListObject(String s, int i, RedBlackTree<NetObject> l) {
        super(s, i);
        list = l;
    }

    /**
     * Make a list object using the data of list l and type i.
     */
    public ListObject(String s, int i, ListObject l) {
        super(s, i);
        list = l.list;
    }

    public ListObject(String s, ListObject l) {
        super(s, l.type);
        list = l.list;
    }

    /**
     * Create a ListObject whose internal tree data structure is set to l.
     *
     * @param l the object that will be set as this ListObject's internal data structure.
     */
    public ListObject(String s, RedBlackTree<NetObject> l) {
        super(s, LIST);
        list = l;
    }

    public ListObject(String s, int i) {
        super(s, i);
        list = new RedBlackTree<>();
    }

    public ListObject(String s) {
        super(s, LIST);
        list = new RedBlackTree<>();
    }

    public NetObject first() {
        if (list != null) {
            final RedBlackTree<NetObject>.RBNode n = list.min();

            if (n != null) {
                @SuppressWarnings("unchecked") final NetObject o = (NetObject) n.getKey().getData();
                return o;
            }
        }

        return null;
    }


    public NetObject last() {
        NetObject o = null;

        if (list != null) {
            RedBlackTree<NetObject>.RBNode n = list.max();

            if (n != null)
                o = n.getKey().getData();
        }

        return o;
    }

    public void set(ListObject n) {
        list.del(new Key<NetObject>(n.name, n));
        list.add(new Key<NetObject>(n.name, n));
    }

    public void set(DataObject n) {
        list.del(new Key<NetObject>(n.name, n));
        list.add(new Key<NetObject>(n.name, n));
    }

    public void add(ListObject n) {
        list.add(new Key<NetObject>(n.name, n));
    }

    public void add(DataObject n) {
        list.add(new Key<NetObject>(n.name, n));
    }

    /**
     * Add the keys in the internal storage of the ListObject <i>lo</i>
     * to this ListObject.
     *
     * @param lo Add all list object to this object.
     */
    public void addTo(final ListObject lo) {
        for (final Key<NetObject> k : lo.list) {
            list.add(k);
        }
    }

    /**
     * Merge the lo ListObject into this object, deleting objects from this
     * ListObject that are in lo.
     * <p>
     * If lo and this list object are the same object, then they are
     * considered already merged.
     *
     * @param lo The list object to merge.
     */
    public void merge(final ListObject lo) {
        // Base-case of merging to ourselves.
        if (this == lo) {
            return;
        }

        for (final Key<NetObject> k : lo.list) {

            if (k.getData().getType() == NetObject.LIST) {

                final ListObject klist = (ListObject) k.getData();

                // Find the list object. If it doesn't exist, set it to klist.
                // merging klist into klist is considered (see first if() above).
                findListObject(klist.getName(), klist).merge(klist);

            } else {
                list.del(k);
                list.add(k);
            }
        }
    }

    /**
     * This will take the internal list of the object <i>lo</i>
     * and replace the internal list of this ListObject with it.
     * This is <b>dangerous</b> because you can create object loops
     * but is a wonderful optimization when reading in objects from files.
     *
     * @param lo The list object to use the internal {@link #list} object.
     */
    public void shareInternalData(final ListObject lo) {
        this.list = lo.list;
    }

    /**
     * Delete the B-Tree substructure and start from scratch.
     */
    public void delAll() {
        list = new RedBlackTree<>();
    }

    /**
     * Create the given path and return the last element in the path.
     *
     * @param path The path.
     * @return The list object at the bottom of the path.
     */
    public ListObject setPath(Object[] path) {
        NetObject nobj = this;  // Net object.
        ListObject lobj = this;  // List object./

        for (int i = 0; i < path.length; i++) {

            String name = path[i].toString();

            try {
                nobj = lobj.find(name);

                /* Is it not a list? */
                if (nobj.getType() != NetObject.LIST) {
                    ListObject tmpobj = new ListObject(name);

                    /* Destroy the object and set it to list. */
                    lobj.set(tmpobj);

                    lobj = tmpobj;

                } else {
                    lobj = (ListObject) nobj;
                }

            } catch (ObjectNotFoundException e) {

                ListObject tmpobj = new ListObject(name);

                lobj.add(tmpobj);

                lobj = tmpobj;
            }
        }

        return lobj;
    }

    /**
     * Create the given path if it does not already exist and set
     * leaf on the last element in the path.
     * For example the path { a, b, c} and the NetObject d would
     * create the list a containing the list b containing the list c
     * containing the object d.
     */
    public void setPath(Object[] path, DataObject leaf) {
        setPath(path).set(leaf);
    }

    public void setPath(Object[] path, ListObject leaf) {
        setPath(path).set(leaf);
    }

    /**
     * If the path exists, return the last element in it.
     */
    public NetObject findPath(Object[] path)
            throws ObjectNotFoundException, MismatchedTypeException {
        ListObject l = findParent(path);

        NetObject no = l.find(path[path.length - 1].toString());

        if (no == null)
            throw new ObjectNotFoundException("Object was not found in parent container.", path[path.length - 1].toString(), l);

        return no;
    }

    /**
     * @param path the path to the object whose parent we are trying to find.
     * @return The list object.
     * @throws ObjectNotFoundException If the object is not found.
     * @throws MismatchedTypeException If it is not the expected type.
     */
    public ListObject findParent(final Object[] path)
            throws ObjectNotFoundException, MismatchedTypeException {
        NetObject nobj = this;  /* Net object. */
        ListObject lobj = this;  /* List object. */
        final int len = path.length - 1;

        for (int i = 0; i < len; i++) {

            nobj = lobj.find(path[i].toString());

            // if we don't find it.
            if (nobj == null)
                throw new ObjectNotFoundException(
                        "Could not find object " + path[i].toString(),
                        path[i].toString(),
                        lobj);

            // if we aren't done looking and this isn't a list object.
            if (nobj.getType() != NetObject.LIST && i < len - 1)
                throw new MismatchedTypeException(
                        "Expected list object " + path[i].toString(),
                        path[i].toString(),
                        lobj);

        }

        return (ListObject) nobj;
    }


  public NetObject find(String n)
  throws ObjectNotFoundException
  {
    Key<NetObject> k = list.find(new Key<NetObject>(n));

    if ( k == null )
      throw new ObjectNotFoundException(
          "Could not find object "+n, n, this);
    
    return k.getData();
  }
  
  public NetObject find(String n, NetObject defaultValue)
  {
    Key<NetObject> k = list.find(new Key<NetObject>(n));

    if ( k != null )
      defaultValue = k.getData();
    
    return defaultValue;
  }

  public NetObject del(String n)
  {
    Key<NetObject> k = list.del(new Key<NetObject>(n));

    return k==null? null : k.getData();
  }
  

  public int size(){ return list.size(); }

    /**
     * Returns a list of Key objects which contain the user data.
     * To access the user data call getData() on the key object.
     * @return A list of all network objects.
     */
  public List<NetObject> toList(){
    List<NetObject> nol = new ArrayList<NetObject>(list.size());

    for ( Key<NetObject> k : list )
      nol.add(k.getData());
      
    return nol;
  }
  
  public void foreach(ListVisitor<NetObject> visitor)
  {
    for ( Key<NetObject> k : list )
      visitor.visit(k.getData());
  }
  
  public void write(OutputStream o) throws IOException
  {
    byte[] header = new byte[10];
    byte[] nameArr = name.getBytes();

    /* Write type id. */
    header[0] = (byte)((type & 0xff000000) >>> 24 );
    header[1] = (byte)((type & 0x00ff0000) >>> 16 );
    header[2] = (byte)((type & 0x0000ff00) >>>  8 );
    header[3] = (byte) (type & 0x000000ff)        ;

    /* Write object name length. */
    header[4] = (byte)((nameArr.length & 0x0000ff00) >>> 8 );
    header[5] = (byte) (nameArr.length & 0x000000ff)        ;

    /* Write data portion length. */
    header[6] = (byte)((list.size() & 0xff000000) >>> 24 );
    header[7] = (byte)((list.size() & 0x00ff0000) >>> 16 );
    header[8] = (byte)((list.size() & 0x0000ff00) >>>  8 );
    header[9] = (byte) (list.size() & 0x000000ff)         ;

    o.write(header);
    
    /* Write the name, then the data. */
    o.write(nameArr);

    for ( Key<NetObject> k : list ) 
      k.getData().write(o);

  }

    /**
     * This will find the ListObject named name in this list object.
     * This will only return defined ListObject, never null.
     *
     * @param name The name.
     * @return The found list object.
     * @throws ObjectNotFoundException When the object is not found.
     * @throws MismatchedTypeException If the type is not a list object.
     */
  public ListObject findListObject(String name)
    throws ObjectNotFoundException, MismatchedTypeException
  {
    NetObject no = find(name);

    if ( no == null )
      throw new ObjectNotFoundException(
        "Cannot find "+name+" in ListObject "+getName(), name, this);

    if ( no.getType() != NetObject.LIST )
      throw new MismatchedTypeException(
        "Object "+name+" in ListObject "+getName()+" is not a DataObject.", name, this);

    return (ListObject) no;
  }
  
    /**
     * Return the found object. If the object is not found or is the wrong
     * type of object, it is replaced with def and def is returned.
     *
     * @param name The name.
     * @param def The default.
     * @return The list of matching names or the default if none are found.
     */
  public ListObject findListObject(String name, ListObject def)
  {
    try
    {
      def = findListObject(name);
    } catch(MismatchedTypeException e) {
      set(def);
    } catch(ObjectNotFoundException e) {
      set(def);
    }

    return def;
  }
  

  /**
   * Returns the list object named <i>name</i> at the bottom of the
   * path.
   * @throws ObjectNotFoundException thrown when the object is not found.
   * @throws MismatchedTypeException thrown when the object found is not a list.
   */
  public ListObject findListObject(Object[] path)
    throws ObjectNotFoundException, MismatchedTypeException
  {
    return findParent(path).findListObject(path[path.length-1].toString());
  }
  
  /**
   * 
   * @param path
   * @param def default value added if name is not found.
   * @return
   */
  public ListObject findListObject(Object[] path, ListObject def)
  {
    try 
    {
      def = findListObject(path);
    }
    catch(Exception e)
    { 
      Object[] path2 = new Object[path.length - 1];
      
      for ( int i = path.length-2; i >= 0; i--)
        path2[i] = path[i];
      
      setPath(path2, def);
    }
    
    return def;
  }
  
 /**
  * This will find the DataObject named name in this list object.
  * This will only return defined DataObjects, never null.
  * If a STRING or LIST object is found a MismatchedTypeException
  * is thrown.
  */
  public DataObject findDataObject(String name)
    throws ObjectNotFoundException, MismatchedTypeException
  {
    NetObject no = find(name);

    if ( no == null )
      throw new ObjectNotFoundException(
        "Cannot find "+name+" in ListObject "+getName(), name, this );

    if ( no.getType() == NetObject.LIST )
      throw new MismatchedTypeException(
        "Object "+name+" in ListObject "+getName()+" is not a DataObject.", name, this);

    return (DataObject) no;
  }
  
  /**
   * @param name data object to find
   * @param def default value added if name is not found.
   * @return
   */
  public DataObject findDataObject(String name, DataObject def)
  {
    try {
      def = findDataObject(name);
    } catch(MismatchedTypeException e) {
      set(def);
    } catch(ObjectNotFoundException e) {
      set(def);
    }
    
    return def;
  }
  
 /**
  * Calls findDataObject. 
  * If the object is not found, this returns an empty string object.
  * If the object is found this will return the conversion of the object. 
  */
  public String findString(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toString();
  }
  
  public String findString(String name, String def)
  {
    try {
      def = findDataObject(name).toString();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }
    
    return def;
  }

  /**
   * Return the double named name or zero if not found.
   */
  public double findDouble(String name)
  throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toDouble();
  }
  
  public double findDouble(String name, double def)
  {
    try {
      def = findDataObject(name).toDouble();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }

    return def;
  }

   
 /**
  * Return the byte named name or zero if not found.
  */
  public byte findInt8(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toInt8();
  }
  
  public byte findInt8(String name, byte def)
  {
    try {
      def = findDataObject(name).toInt8();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }

    return def;
  }


  
 /**
  * Return the short named name or zero if not found.
  */
  public short findInt16(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toInt16();
  }

  
  public short findInt16(String name, short def)
  {
    try {
      def = findDataObject(name).toInt16();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }

    return def;
  }

 /**
  * Return the int named name or zero if not found.
  */
  public int findInt32(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toInt32();
  }
  
  public int findInt32(String name, int def)
  {
    try {
      def = findDataObject(name).toInt32();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }

    return def;
  }
  
 /**
  * Return the long named name or zero if not found.
  */
  public long findInt64(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toInt64();
  }
  
  public long findInt64(String name, long def)
  {
    try {
      def = findDataObject(name).toInt64();
    } catch(MismatchedTypeException e) {
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }

    return def;
  }
 
 /**
  * Return the byte array named name or null if not found.
  */
  public byte[] findByteArray(String name)
    throws MismatchedTypeException, ObjectNotFoundException
  {
    return findDataObject(name).toByteArray();
  }
  
  public byte[] findByteArray(String name, byte[] def)
  {
    try {
      def = findDataObject(name).toByteArray();
    } catch(MismatchedTypeException e){
      set(new DataObject(name, def));
    } catch(ObjectNotFoundException e) {
      set(new DataObject(name, def));
    }
    
    return def;
  }
  
 /**
  * This returns an iterator class implementing the Iterable interface.
  * The returned Iterator is really a wrapper around the RedBlackTree 
  * iterator in which next() unwraps the keys to expose the user data.
  */
  public Iterator<NetObject> iterator()
  {

    return new Iterator<NetObject>()
    {
      protected Iterator<Key<NetObject>> i = list.iterator();

      public boolean   hasNext() { return i.hasNext(); }
      public NetObject next()    { return i.next().getData(); }
      public void      remove()  { i.remove(); }
    };
  }
}
   
