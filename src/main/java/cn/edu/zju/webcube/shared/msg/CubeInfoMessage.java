package cn.edu.zju.webcube.shared.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeAggregationType;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.db.HiveDataType;
import cn.edu.zju.webcube.shared.db.Table;

/**
 * message for cube metadata
 * 
 * @author wusai
 *
 */
public class CubeInfoMessage implements Message {

	private CubeManager cube;
	private static final String SEP_1 = "#";
	private static final String SEP_2 = "&";
	private static final String SEP_3 = ",";

	public CubeInfoMessage(String serializedData) {
		parse(serializedData);
	}

	public CubeInfoMessage(CubeManager cube) {
		this.cube = cube;
	}

	public CubeManager getCube() {
		return cube;
	}

	@Override
	public void parse(String serializedData) {
		 HashMap<Column, Integer> invertedMap = new HashMap<Column,Integer>();
		 HashMap<Column, Integer> invertedGroup = new HashMap<Column,Integer>();
		 ArrayList<String> tableNames= new ArrayList<String>();
		 ArrayList<Table> tables= new ArrayList<Table>();
		 
		 String[] segs = serializedData.split(SEP_1);
		 String cubeId = segs[0];
		 
		 // invertedMap string
		 String invertedMapStr = segs[1];
		 String[] columnStr = invertedMapStr.split(SEP_3);
		 
		 for(String col : columnStr) {
			 String[] colAttr = col.split(SEP_2);
			 String tableName = colAttr[0];
			 String colName = colAttr[1];
			 String type = colAttr[2];
			 String agg = colAttr[3];
			 String index = colAttr[4];
			 Column newCol = new Column(tableName, colName, type);
			 
			 if(agg == null || agg.equals("null")) {
				 newCol.setAsOrdinaryType();
			 } else {
				 newCol.setAsQuantityType();
				 newCol.setRequiredAgg(agg);
			 }
			 
			 invertedMap.put(newCol, Integer.valueOf(index));
			 
			 if(!tableNames.contains(tableName)) {
				 tableNames.add(tableName);
				 tables.add(new Table(tableName));
			 }
		 }
		 
		 //invertedGroup string
		 String invertedGroupStr = segs[2];
		 columnStr = invertedGroupStr.split(SEP_3);
		 for(String col : columnStr) {
			 String[] colAttr = col.split(SEP_2);
			 String tableName = colAttr[0];
			 String colName = colAttr[1];
			 String type = colAttr[2];
			 String index = colAttr[3];
			 Column newCol = new Column(tableName, colName, type);
			 newCol.setAsOrdinaryType();
			 invertedGroup.put(newCol, Integer.valueOf(index));
			 
			 if(!tableNames.contains(tableName)) {
				 tableNames.add(tableName);
				 tables.add(new Table(tableName));
			 }
		 }
		 
		 CubeManager cube = new CubeManager(cubeId, invertedMap, invertedGroup);
		 
		 for(Table t : tables) {
			 cube.addDimensionTable(t,new ArrayList<Column>(), new ArrayList<Column>());
		 }
		 
		 this.cube = cube;
		 

	}

	@Override
	/**
	 * cubeID#tablename&columname&type&agg&index,[...]#tablename&columname&type&index,[...]
	 * ------#---invertedMap--------------------------#----------invertedGroup--------
	 */
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		int size = 0;
		int counter = 0;
		
		sb.append(cube.getID());
		sb.append(SEP_1);

		// add invertedMap tablename|columnname|agg|index
		HashMap<Column, Integer> invertedMap = cube.getInvertedMap();
		Set<Column> mapset = invertedMap.keySet();
		size = mapset.size();
		counter = 1;
		for (Column key : mapset) {
			String record = key.getTable() + SEP_2 + key.getName() + SEP_2 + key.getType() + SEP_2 + key.getRequiredAgg() + SEP_2 + invertedMap.get(key);
			sb.append(record);
			if (counter != size) {
				sb.append(SEP_3);
			}
			counter++;
		}
		sb.append(SEP_1);

		//add invertedGroup tableName|columnName|index
		HashMap<Column, Integer> invertedGroup = cube.getInvertedGroup();
		Set<Column> groupset = invertedGroup.keySet();
		size = groupset.size();
		counter = 1;

		for (Column key : groupset) {
			String record = key.getTable() + SEP_2 + key.getName() + SEP_2 + key.getType() + SEP_2 + invertedGroup.get(key);
			sb.append(record);
			if (counter != size) {
				sb.append(SEP_3);
			}
			counter++;
		}

		return sb.toString();
	}

}
