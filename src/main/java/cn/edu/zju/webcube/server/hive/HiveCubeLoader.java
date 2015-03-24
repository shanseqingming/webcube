package cn.edu.zju.webcube.server.hive;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.zju.webcube.server.mysql.MysqlMetaLoader;
import org.apache.commons.dbutils.DbUtils;

import cn.edu.zju.webcube.server.utils.BasicJdbcLoader;
import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.db.CubeResult;
import org.apache.log4j.Logger;


public class HiveCubeLoader extends BasicJdbcLoader {

	private static final Logger logger = Logger.getLogger(HiveCubeLoader.class);
	public static ArrayList<ColumnValueView> dimensionbuffer = new ArrayList<>();
	private MysqlMetaLoader metaLoader = new MysqlMetaLoader("mysql");

	public HiveCubeLoader(String databaseType) {
		super(databaseType);
	}

	/**
	 *
	 * @param sql
	 * @param cube
	 */
	public void createCube(String sql, CubeManager cube) {
		
		Connection conn = null;
		Statement stmt = null;

		sql = "create table hive_cube_" + cube.getID() + " as " + sql;
		
		try {
			conn = getDBConnection(false);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			metaLoader.saveCubeBuffer(cube); //save the meta to mysql
			
		} catch (SQLException | IOException e) {
			logger.error(e.getMessage());

		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
		
	}

	

	public ArrayList<CubeManager> getAllCubes(){
		HashMap<String, CubeManager> cubeBuffer = metaLoader.getAllCubeMeta();
		ArrayList<CubeManager> cubes = new ArrayList<>();

		for(String key: cubeBuffer.keySet()) {
			cubes.add(cubeBuffer.get(key));
		}
		return cubes;
	}
	
	public CubeManager getCube(String cubeId) {
		return metaLoader.getCubeMeta(cubeId);
	}

	
	private int queryCardinalityOfDimensionFromBuffer(Column c) {
		for(ColumnValueView cvv : dimensionbuffer) {
			if(cvv.column.compare(c)) {
				return cvv.count;
			}
		}
		return -1;
	}


	/**
	 *
	 * @param c
	 * @return
	 */
	public int queryCardinalityOfDimension(Column c) {
		
		int count = queryCardinalityOfDimensionFromBuffer(c);
		if(count > 0) {
			return count;
		}
		
		// all distinct values of this column
		ArrayList<String> values = new ArrayList<String>(); 
		
		String sql = "select distinct "+c.getName()+" from " + c.getTable() ;
		logger.info("\nsql:  "+sql+"\n");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String value;
		
		try {			
			conn = this.getDBConnection(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				value = rs.getString(1);
				System.out.print(value+"\n");
				if(value == null || value.equalsIgnoreCase("null")) {
					continue;
				}
				values.add(value);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 DbUtils.closeQuietly(rs);
			 DbUtils.closeQuietly(stmt);
			 DbUtils.closeQuietly(conn);
		}
		
		if(values.size() > 0) {
			ColumnValueView cvv = new ColumnValueView();
			cvv.column = c;
			cvv.count = values.size();
			cvv.values = values;
			dimensionbuffer.add(cvv);
		}
		
		return values.size();
	}
	
	
	public ArrayList<String> queryValuesOfDimension(Column column) {
		
		//get values from buffer
		for(ColumnValueView cvv : dimensionbuffer ) {
			
			if(cvv.column.compare(column)) {
				return cvv.values;
			}
		}
		
		//get values from database
		if(queryCardinalityOfDimension(column) > 0 ) {
			return queryValuesOfDimension(column); 
		} else {
			return null;
		}
	}
	
	
	public ArrayList<String> queryValuesOfDimension(Column columnName, int offset, int count) {

		ArrayList<String> sublist;
		ArrayList<String> valueList;
		
		for (ColumnValueView cvv : dimensionbuffer) {
			System.out.print("column name "+cvv.column.getName()+"\n");
			if (cvv.column.compare(columnName)) {
				sublist = new ArrayList<>();
				valueList = cvv.values;
				for(int i = offset; i< valueList.size() && i<offset + count; i++) {
					sublist.add(valueList.get(i));
				}
				return sublist;
			}
		}

		if(queryCardinalityOfDimension(columnName) > 0 ) {
			return queryValuesOfDimension(columnName, offset, count); 
		} else {
			return null;
		}
	}
	
	
	
	public ArrayList<CubeResult> queryCubePlus(String cubeID, ArrayList<Column> groupColumn, ArrayList<Column> aggColumn) {
		
		ArrayList<CubeResult> results = new ArrayList<>();
		
		ArrayList<Column> groups1 = new ArrayList<>();
		ArrayList<Column> groups2 = new ArrayList<>();
		groups1.add(groupColumn.get(0));
		groups2.add(groupColumn.get(1));
		
		ArrayList<CubeResult> result1 = queryCube(cubeID, groups1, aggColumn);
		if(result1 != null) {			
			results.addAll(result1);
		}
		ArrayList<CubeResult> result2 = queryCube(cubeID, groups2, aggColumn);
		if(result2 != null) {			
			results.addAll(result2);
		}
		
		if(results.size() == 0) {
			return null;
		}
		
		return results;
	}
	
	
	public ArrayList<CubeResult> queryCubeSlash(String cubeID, ArrayList<Column> groupColumn, ArrayList<Column> aggColumn) {
		return queryCube(cubeID, groupColumn, aggColumn);
	}
	
	public ArrayList<CubeResult> queryCubeStar(String cubeID, ArrayList<Column> groupColumn, ArrayList<Column> aggColumn) {
		
		ArrayList<CubeResult> results = queryCube(cubeID, groupColumn, aggColumn);
		
		if(results == null) 
			return null;
		
		ArrayList<String> existGroupCombination = new ArrayList<String>();
		ArrayList<String> allGroupCombination;
		
		for(CubeResult res : results) {
			String combination = "";
			for(Column col : res.getAllGroupNames()) {
				combination += res.getGroupValue(col) + ".";
			}
			existGroupCombination.add(combination.substring(0, combination.length()-1));
		}
		
		//CubeResult中的group的顺序和groupColumn传进来的顺序有可能不一样。因为HashMap不能保证put进来的顺序就是最终的顺序
		ArrayList<Column> groupsColumnWithNewOrder = new ArrayList<Column>();
		groupsColumnWithNewOrder.addAll(results.get(0).getAllGroupNames());
		
		allGroupCombination = getAllCombination(groupsColumnWithNewOrder);
		
		
		boolean flag;
		for(String all : allGroupCombination) {
			flag = true;
			for(String exist : existGroupCombination) {
				if(exist.equalsIgnoreCase(all)) {
					flag = false;
					break;
				}
			}
			
			if(flag) {				
				
				CubeResult cu = new CubeResult();
				//int count = 0;
				//cu.setCount(count);
				
				for(int i=0; i<groupsColumnWithNewOrder.size(); i++) {
					cu.addGroupValue(groupsColumnWithNewOrder.get(i), all.split("\\.")[i]); //BUG 要判断这个column具体属性
				}
				
				for(int i=0; i<aggColumn.size(); i++) {
					cu.addAggValue(aggColumn.get(i),  "null");
				}
				results.add(cu);
			}
		}
			
		return results;
	}
	
	
	private ArrayList<String> getAllCombination(ArrayList<Column> columns) {
		if(columns.size() > 2)
			return null;
		
		ArrayList<String> allCombination = new ArrayList<String>();
		ArrayList<String> v0 = queryValuesOfDimension(columns.get(0));
		ArrayList<String> v1 = queryValuesOfDimension(columns.get(1));

		for(String g0 : v0) {
			for(String g1 : v1) {
				allCombination.add(g0 + "." + g1);
			}
		}
		
		return allCombination;
	}


	/**
	 *
	 * @param cubeID
	 * @param groupColumn
	 * @param aggColumn
	 * @return
	 */
	
	public ArrayList<CubeResult> queryCube(String cubeID, ArrayList<Column> groupColumn, ArrayList<Column> aggColumn) {
		CubeManager cube = metaLoader.getCubeMeta(cubeID);
		if(cube == null)
			return null;
		
		String cubeTable = "hive_cube_" + cubeID;
		
		// index of group column in a record of the  hive cube table 
		ArrayList<Integer> groupColumn_idx = new ArrayList<Integer>();
		
		// index of agg column in a record of the  hive cube table 
		ArrayList<Integer> aggColumn_idx = new ArrayList<Integer>();
		
		HashMap<Column, Integer> groups = cube.getInvertedGroup();
		
		//all columns map to a index in hive cube table
		HashMap<Column, Integer> allColumns = cube.getInvertedMap(); 
		
		ArrayList<CubeResult> results = new ArrayList<CubeResult>(); //for results returning
		
		//get grouping__id
		int grouping__id = 0;
		
		for (Column gc : groupColumn) {
			for(Column gColumn : groups.keySet()) {
				if(gc.compare(gColumn)) {
					int idx = groups.get(gColumn);
					grouping__id += Math.pow(2, idx);
					break;
				}
			}
			
			for(Column c : allColumns.keySet()) {
				if(gc.compare(c)) {
					int idx = allColumns.get(c);
					groupColumn_idx.add(idx);
					break;
				}
			}	
		}
		
		
		for(Column ac : aggColumn) {
			for(Column c : allColumns.keySet()) {
				if(ac.compare(c)) {
					int idx = allColumns.get(c);
					aggColumn_idx.add(idx);
				}
			}
		}

		String sql = "select * from " + cubeTable + "  where GROUPING__ID=" + grouping__id + " ";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {			
			conn = this.getDBConnection(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				CubeResult cu = new CubeResult();
				//int count = rs.getInt(1);
				//cu.setCount(count);
				
				for(int i=0; i<groupColumn.size(); i++) {
					cu.addGroupValue(groupColumn.get(i), rs.getString(groupColumn_idx.get(i)));
				}
				
				for(int i=0; i<aggColumn.size(); i++) {
					cu.addAggValue(aggColumn.get(i), rs.getString(aggColumn_idx.get(i)));
				}
				results.add(cu);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 DbUtils.closeQuietly(rs);
			 DbUtils.closeQuietly(stmt);
			 DbUtils.closeQuietly(conn);
		}
		
		if(results.size() == 0) {
			return null;
		}
		
		return results;
	}


	private static class ColumnValueView {
		public Column column = null;
		public int count = 0; //how many distinct value of this column
		public ArrayList<String> values = null;
	}
}

