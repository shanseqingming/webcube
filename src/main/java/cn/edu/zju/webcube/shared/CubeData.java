package cn.edu.zju.webcube.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.db.CubeRecord;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.Table;

/**
 * 
 * @author wusai
 *
 */

public class CubeData {

	private static CubeRecord[] records;
	
	public static CubeRecord[] getRecords() {  
        if (records == null) {  
            records = getNewRecords();  
        }  
        return records;  
    } 
	
	 public static CubeRecord[] getNewRecords() {  
	     /**
	      * TODO parse the information from server and generate the records;   
	      */
		 HashMap<String, CubeManager> cubes = MetaDataBuffer.getInstance().getCubes();
		 Set<String> keys = cubes.keySet();
		 
		 CubeRecord[] data = new CubeRecord[cubes.size()];
		 int i = 0;
		 for(String key : keys){
			 String cubeID = "cube: "+key;
			 String tableInfo = "for table: ";
			 CubeManager cube = cubes.get(key);
			 ArrayList<Table> tables = cube.getDimensionTable();
			 for(Table table: tables){
				 tableInfo += table.getName() + " ";
			 }
			 data[i] = new CubeRecord(cubeID, tableInfo);
			 i++;
		 }		 
		 return data;  
	    }  
	
}
