package cn.edu.zju.webcube.shared.msg;

import java.util.ArrayList;


/**
 * The result message of a query.
 * All results are buffered in memory which may lead to memory
 * overflow if too many results are returned. A better solution
 * should be used instead.
 * @author wusai
 *
 */
public class QueryResultMessage implements Message{
	
	private String[] header;
	
	private String[][] records;
	
	private int numberOfResults;
	
	public QueryResultMessage(String serializedData){
		parse(serializedData);
	}
	
	public QueryResultMessage(int numberOfColumns){
		numberOfResults = 0;
		header = new String[numberOfColumns];
	}
	
	public void setHeader(String columnName, int idx){
		if(idx < header.length){
			header[idx] = columnName;
		}
	}
	
	public void addNewResult(ArrayList<String> allResults){
		records = new String[allResults.size()][];
		for(int i=0; i<allResults.size(); i++){
			records[i] = allResults.get(i).split("\\|"); 
		}
	}
	
	public String[] getHeader(){
		return header;
	}
	
	public int getResultNumber(){
		return numberOfResults;
	}
	
	public String[] getRecord(int idx){
		if(idx<numberOfResults){
			return records[idx];
		}
		else
			return null;
	}

	@Override
	public void parse(String serializedData) {
		// TODO Auto-generated method stub
		
		String[] allResults = serializedData.split(",");
		numberOfResults = Integer.parseInt(allResults[0]);
		
		//first get header
		header = allResults[1].split("\\|");
		//then, get all records
		records = new String[numberOfResults][];
		for(int i=0; i<numberOfResults; i++){
			records[i] = allResults[i+2].split("\\|");
		}
		
	}

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		String reval = numberOfResults + ",";
		
		for(int i=0; i<header.length-1; i++){
			reval += header[i] + "|";
		}
		reval += header[header.length-1] + ",";
		
		for(int i=0; i<numberOfResults; i++){
			for(int j=0; j<records[i].length-1; j++){
				reval += records[i][j] + "|";
			}
			reval += records[i][records[i].length-1] + ",";
		}
		
		return reval.substring(0, reval.length()-1);
	}

}
