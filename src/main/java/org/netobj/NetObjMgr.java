/* $Id: NetObjMgr.java 674 2008-05-07 03:57:19Z sam $ */

package org.netobj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.sdsai.List;

public class NetObjMgr 
{

  public static final int WRITE = 0;
  public static final int READ  = 1;

  public static void main(String[] argv)
  {
    String  infile   = "netobj.file";
    String  outfile  = "netobj.file";
    String  value    = "";
    int     action   = WRITE;
    boolean helpflag = false;
    int     i        = 0; 
    int     pathi    = 0;
   
    for(i=0; i<argv.length; i++){
      if(argv[i].equals("-h")){

        helpflag = true;
        i        = argv.length;

        
      /* Are there at least 2 array values left? */
      } else if(argv[i].equals("-p")){

        action = READ;

      } else if(argv.length-2 >= i){

        if(argv[i].equals("-i")){

          infile = argv[++i];

        } else if(argv[i].equals("-o")){

          outfile = argv[++i];

        } else if(argv[i].equals("-s")){
          
          value = argv[++i];

        } else {
          pathi = i;
          i     = argv.length;
        }

      /* If we can't find a flag, we assume the flags are done and the path
       * has started. */
      } else {
        
        pathi = i;
        i     = argv.length;
        
      }
    }

    if(argv.length == 0 || helpflag){
      System.out.println("Ussage: NetObjMsg [options] [obj name]\n"+
                         "-i in    - read from this file\n"+
                         "-o out   - write to this file after reading.\n"+
                         "-s val   - set this value in the given object\n"+
                         "-h       - this screen\n"+
                         "-p       - print the file contents only\n"+
                         "obj name - path to the object separated by spaces\n"
                         );
      System.exit(1);
    }

    try {
      NetObject root   = null;
      NetObject netobj = null;
      
      if(action==WRITE && pathi+1 == argv.length){
        
        root = new DataObject(argv[pathi], value);
        
      } else {
        
        /* Find the netobj value to set. If ever an object is not found, 
         * it is added. */
        try {
          
          FileInputStream in  = new FileInputStream(new File(infile));
          
          NetObjectReader<DataObject, ListObject> nor = new NetObjectReader<DataObject, ListObject>(in);
          
          root     = nor.readNetObject();

          netobj   = root;
        
          in.close();

        } catch(FileNotFoundException e){
        }

      }

      if(action == READ){

        printObj(root);

      } else if(action == WRITE){

        /* Condidtions to make a new root. */
        if(root==null || 
           ! root.getName().equals(argv[pathi]) ||
           root.getType() != NetObject.LIST){

          root = new ListObject(argv[pathi]);
          netobj = root;

        }

        pathi++;

        for(; pathi < argv.length; pathi++){

          /* If this is the last object, aka, not a list. */
          if(pathi + 1 == argv.length){

            /* Set the value. */
            ((ListObject)netobj).set(new DataObject(argv[pathi], value));

          } else {
            
            ListObject tmpLst;
            try {
              
              tmpLst = ((ListObject)root).findListObject(argv[pathi]);
              
            } catch(MismatchedTypeException e) {
              
              tmpLst = new ListObject(argv[pathi]);
              ((ListObject)netobj).add(tmpLst);
              
            } catch(ObjectNotFoundException e) {
              
              tmpLst = new ListObject(argv[pathi]);
              ((ListObject)netobj).add(tmpLst);
              
            }
            
            /* step down one level in the tree of lists. */
            netobj = tmpLst;
          
          }

        }
        
      

      
        FileOutputStream out = new FileOutputStream(outfile, false);
        root.write(out);
        out.close();
      }

    } catch(FileNotFoundException e){

      System.out.print(e.getMessage());
      System.exit(1);

    } catch(MalformedObjectException e){

      System.out.print(e.getMessage());
      System.exit(1);

    } catch(IOException e){

      System.out.print(e.getMessage());
      System.exit(1);

    }
    
  }


  public static void printObj(NetObject no)
  {
    if(no!=null)
      printObjHelper(no, "");
  }

  public static String typeToString(int type)
  {
    if ( type == NetObject.LIST )
      return "List";
    if ( type == NetObject.STREAM_SEG )
      return "Stream Segment";
    if ( type == NetObject.LAST_STREAM_SEG )
      return "Last Stream Segment";
    if ( type == NetObject.INT8 )
      return "int8";
    if ( type == NetObject.INT16 )
      return "int16";
    if ( type == NetObject.INT32 )
      return "int32";
    if ( type == NetObject.INT64 )
      return "int64";
    if ( type == NetObject.STRING )
      return "String";
    if ( type == NetObject.DATA )
      return "Data";

    return "Unknown Type "+type;
  }

  public static void printObjHelper(NetObject no, String prefix)
  {
    if(no.getType() == NetObject.LIST){

      System.out.println(prefix + "|--" + no.getName() + " : " +
                           typeToString(no.getType()));

      List<NetObject> l = ((ListObject)no).toList();

      for(int i = 0; i < l.size(); i++){
        
        printObjHelper((NetObject)l.get(i), prefix+"|  ");

      }

    } else {
      System.out.println(prefix+"|--"+ no.getName() + " : " + 
                         typeToString(no.getType()) + " = " + 
                         toString(no));
    }
  }
  
  public static String toString(NetObject o)
  {
    int type = o.getType();
    
    if(type == NetObject.STRING)
        return ((DataObject)o).toString();
    if(type == NetObject.INT8)
      return ""+((DataObject)o).toInt8();
    if(type == NetObject.INT16)
      return ""+((DataObject)o).toInt16();
    if(type == NetObject.INT32)
      return ""+((DataObject)o).toInt32();
    if(type == NetObject.INT64)
      return ""+((DataObject)o).toInt64();

    return "[unknown or handled object type "+o.getType()+"]";
  }  
}
