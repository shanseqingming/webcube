package cn.edu.zju.webcube.shared.json.view;

import java.util.HashMap;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeManager;

public class CubeManagerView {
	private String cubeID;
	private HashMap<String, ColumnInfo> columnsInfo = new HashMap<String, ColumnInfo>();

	public CubeManagerView(CubeManager cube) {
		this.cubeID = cube.getID();
		HashMap<Column, Integer> invertedMap = cube.getInvertedMap();
		for (Column c : invertedMap.keySet()) {
			columnsInfo.put(c.getName(), new ColumnInfo(c));
		}

	}

	public String getCubeID() {
		return cubeID;
	}

	public void setCubeID(String cubeID) {
		this.cubeID = cubeID;
	}

}

class ColumnInfo {
	private String tableName;
	private String dataType;
	private String columnType;

	ColumnInfo(Column c) {
		tableName = c.getTable();
		dataType = c.getType().toString();
		columnType = c.getCubeColumnType().toString();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

}
