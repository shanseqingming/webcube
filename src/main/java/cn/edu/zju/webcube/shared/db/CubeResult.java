package cn.edu.zju.webcube.shared.db;

import java.util.HashMap;
import java.util.Set;

/**
 * the query result of a data cube may contain a
 * set of records, one for each specific group
 * @author wusai
 *
 */
public class CubeResult {

	//private int count = 0;
	
	private HashMap<Column, String> groupValue = new HashMap<Column, String>();
	
	private HashMap<Column, String> aggregationValue = new HashMap<Column, String>();
	
	
	
	public CubeResult(){
		
	}
	
	/*public void setCount(int c){
		count = c;
	}
	
	public int getCount(){
		return count;
	}*/
	
	public void addGroupValue(Column column, String value){
		groupValue.put(column, value);
	}
	
	public void addAggValue(Column column, String value){
		aggregationValue.put(column, value);
	}
	
	
	public String getGroupValue(Column column){
		return groupValue.get(column);
	}
	
	public String getAggValue(Column column){
		return aggregationValue.get(column);
	}
	
	public Set<Column> getAllGroupNames(){
		return groupValue.keySet();
	}
	
	public Set<Column> getAllAggregationNames(){
		return aggregationValue.keySet();
	}
}
