package cn.edu.zju.webcube.shared.db;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * a table in database
 * @author wusai
 *
 */
public class Table {

	private String name;
	
	private int NumberOfColumns = 0;
	
	private ArrayList<Column> columns = new ArrayList<Column>();
	
	private int primarykeyIndex = -1;
	
	private HashMap<Integer, String> foreignKeyMap = new HashMap<Integer, String>();
	
	private boolean isFactTable = false;
	
	
	public Table(String name){
		this.name = name;
	}
	
	public void addColumn(Column c){
		columns.add(c);
		NumberOfColumns ++;
	}
	
	public void setPrimaryKey(int idx){
		if(idx>0 && idx<columns.size())
			primarykeyIndex = idx;
	}
	
	public void setPrimaryKey(String columnName){
		int idx = hasColumn(columnName);
		if(idx>=0)
			primarykeyIndex = idx;
	}
	
	public Column getPrimaryKey(){
		return columns.get(primarykeyIndex);
	}
	
	public int getNumberOfColumns(){
		return NumberOfColumns;
	}
	
	public boolean isFactTable(){
		return isFactTable;
	}
	
	public void setAsFactTable(){
		isFactTable = true;
	}
	
	public String getName(){
		return name;
	}
	
	public int hasColumn(String columnName){
		
		int idx = 0;
		for(Column col : columns){
			if(col.getName().equalsIgnoreCase(columnName))
				return idx;
			else 
				idx++;
		}
		return -1;
	}
	
	public Column getColumn(String columnName){
		
		int idx = hasColumn(columnName);
		if(idx>=0)
			return columns.get(idx);
		else
			return null;
	}
	
	public ArrayList<Column> getAllColumns(){
		return columns;
	}
	
	public boolean isForeignKey(String columnName){
		int idx = hasColumn(columnName);
		if(idx>=0){
			return foreignKeyMap.containsKey(idx);
		}
		else return false;
	}
	
	public String[] getJoinableColumn(String columnName){
		int idx = hasColumn(columnName);
		if(idx>=0){
			String[] rev = new String[2];
			String alias = foreignKeyMap.get(idx);
			int pos = alias.lastIndexOf('.');
			rev[0] = alias.substring(0, pos);
			rev[1] = alias.substring(pos+1, alias.length());
			return rev;
		}
		else return null;
	}
	
	/**
	 * A column of this table is the primary key of the "connectedTable" with the
	 * alias name "aliasName" in that table
	 * @param columnName
	 * @param connectedTable
	 * @param aliasName
	 */
	public void setForeignKey(String columnName, String connectedTable, String aliasName){
		int idx = hasColumn(columnName);
		if(idx>=0){
			//did not check whether connectedTable.aliasName exists!!!
			foreignKeyMap.put(idx, connectedTable+"."+aliasName);
		}
	}
	
	/**
	 * To a serialized string
	 * note: in the future, should use json format
	 */
	@Override
	public String toString(){
		String reval = "";
		reval = name  + "|" + NumberOfColumns + "|";
		for(int i=0; i<NumberOfColumns; i++){
			Column column = columns.get(i);
			
			reval += column.getName() + ":";
			reval += column.getType()+"|";
			/**
			switch(column.getType()){
				case Column.BOOLEAN_TYPE : reval += "boolean|"; break;
				case Column.DATE_TYPE : reval += "date|"; break;
				case Column.DOUBLE_TYPE: reval += "double|"; break;
				case Column.FLOAT_TYPE: reval +=  "float|"; break;
				case Column.INT_TYPE: reval += "int|"; break;
				case Column.STRING_TYPE: reval += "string|"; break;
				default:
					reval += "unknown|"; break;
				
				}**/
		}
		
		return reval;
		
	}
}
