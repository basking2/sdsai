package com.github.basking2.sdsai.netobj;

public abstract class 
NetObjectFactory<DATATYPE extends DataObject, LISTTYPE extends ListObject>
{
  private static NetObjectFactory<DataObject, ListObject> 
  defInstance = new NetObjectFactory<DataObject, ListObject>()
  {
    public DataObject createDataObject(String name, int type, byte[] data)
    {
      return new DataObject(name, type, data);
    }

    public ListObject createListObject(String name, int type)
    {
      return new ListObject(name, type);
    }
  };

  public static NetObjectFactory<DataObject, ListObject> getDefaultFactory()
  {
    return defInstance;
  }

  public abstract DATATYPE createDataObject(String name, int type, byte[] data);
  public abstract LISTTYPE createListObject(String name, int type);
  
}
