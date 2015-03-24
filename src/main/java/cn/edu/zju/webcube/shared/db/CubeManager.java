package cn.edu.zju.webcube.shared.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * create a data cube for a fact table and multiple its dimension tables
 * 
 * @author wusai
 *
 */
public class CubeManager {

	private String cubeID;

	private Table factTable;

	private ArrayList<Table> dimensionTables = new ArrayList<Table>();

	private final int baseIndex = 2; // where is the position of first column in the result table

	private HashMap<Column, Integer> invertedMap = new HashMap<Column, Integer>(); //所有column都记录

	private HashMap<Column, Integer> invertedGroup = new HashMap<Column, Integer>(); //只记录那些group的column


	public CubeManager(String cubeID, HashMap<Column, Integer> invertedmap,
			HashMap<Column, Integer> invertedgroup) {
		this.cubeID = cubeID;
		invertedMap = invertedmap;
		invertedGroup = invertedgroup;
	}

	public CubeManager(String cubeID, Table fact) {
		this.cubeID = cubeID;
		factTable = fact;
	}

	public void updateCubeID(String ID) {
		cubeID = ID;
	}

	public String getID() {
		return cubeID;
	}

	public void setFactTable(Table fact) {
		factTable = fact;
	}

	public void setFactPrimaryKey(String columnName) {
		factTable.setPrimaryKey(columnName);
	}

	public void addDimensionTable(Table dimTable,
			ArrayList<Column> foreignKeys, ArrayList<Column> alias) {
		dimensionTables.add(dimTable);
		if (foreignKeys != null && alias != null) {
			assert (foreignKeys.size() == alias.size());
			for (int i = 0; i < foreignKeys.size(); i++) {
				Column fk = foreignKeys.get(i);
				Column ali = alias.get(i);
				if (dimTable.hasColumn(ali.getName()) >= 0)
					factTable.setForeignKey(fk.getName(), dimTable.getName(),
							ali.getName());
			}
		}
	}

	public void addDimensionTable(Table dimTable, String foreignKeys,
			String alias) {
		dimensionTables.add(dimTable);
		if (dimTable.hasColumn(alias) >= 0) {
			factTable.setForeignKey(foreignKeys, dimTable.getName(), alias);
		}
	}

	public Table getTableByName(String tname) {
		if (tname.equalsIgnoreCase(factTable.getName()))
			return factTable;
		else {
			for (Table nextTable : dimensionTables) {
				if (nextTable.getName().equalsIgnoreCase(tname)) {
					return nextTable;
				}
			}
			return null;
		}
	}

	public void setAsOrdinaryColumn(String tname, ArrayList<String> ordinary) {
		Table table = getTableByName(tname);
		if (table != null && ordinary != null) {
			for (String columnName : ordinary) {
				Column column = table.getColumn(columnName);
				if (column != null)
					column.setAsOrdinaryType();
			}
		}
	}

	public void setAsQuantityColumn(String tname, ArrayList<String> quantity) {
		Table table = getTableByName(tname);
		if (table != null && quantity != null) {
			for (String columnName : quantity) {
				Column column = table.getColumn(columnName);
				if (column != null)
					column.setAsQuantityType();
			}
		}
	}

	public String generateCubeSQL() {

		String sql = "select count(*), GROUPING__ID, ";

		String select = "";
		String from = "";
		String where = "";
		String groupby = "";

		// start from fact table and iterate through all dimension tables
		ArrayList<Column> columnsOfFactTable = factTable.getAllColumns();

		from += factTable.getName() + ",";
		int columnIdx = baseIndex;
		int groupIdx = -1;
		for (Column nextColumn : columnsOfFactTable) {
			String name = factTable.getName() + "." + nextColumn.getName();
			if (nextColumn.isOrdinary()) {
				groupby += name + ",";
				select += name + ",";
				columnIdx++;
				groupIdx++;
				invertedMap.put(nextColumn, columnIdx);
				invertedGroup.put(nextColumn, groupIdx);
			} else if (nextColumn.isQuantity()) {
				
				for(CubeAggregationType type: CubeAggregationType.values()) {
					Column c = Column.copyOf(nextColumn);
					c.setRequiredAgg(type);
					select += type.toString() + "(" + c.getTable() + "." + c.getName() + "),";
					invertedMap.put(c, ++columnIdx);
				}

			} 
			if (factTable.isForeignKey(nextColumn.getName())) {
				String[] rev = factTable
						.getJoinableColumn(nextColumn.getName());
				where += name + "=" + rev[0] + "." + rev[1] + ",";
			}
		}

		for (Table table : dimensionTables) {
			from += table.getName() + ",";
			for (Column nextColumn : table.getAllColumns()) {
				String name = table.getName() + "." + nextColumn.getName();
				if (nextColumn.isOrdinary()) {
					groupby += name + ",";
					select += name + ",";
					columnIdx++;
					groupIdx++;
					invertedMap.put(nextColumn, columnIdx);
					invertedGroup.put(nextColumn, groupIdx);
				} else if (nextColumn.isQuantity()) {
					for(CubeAggregationType type: CubeAggregationType.values()) {
						Column c = Column.copyOf(nextColumn);
						c.setRequiredAgg(type);
						select += type.toString() + "(" + c.getTable() + "." + c.getName() + "),";
						invertedMap.put(c, ++columnIdx);
					}
				}
			}
		}

		// generate the final sql

		sql += select.substring(0, select.length() - 1) + " from "
				+ from.substring(0, from.length() - 1);

		if (where.length() > 0) {
			sql += " where " + where.substring(0, where.length() - 1)
					+ " group by " + groupby.substring(0, groupby.length() - 1)
					+ " with cube";
		} else {
			sql += " group by " + groupby.substring(0, groupby.length() - 1)
					+ " with cube";
		}
		System.out.println(sql);
		return sql;
	}
	
	public Table getFactTable() {
		return factTable;
	}

	public HashMap<Column, Integer> getInvertedMap() {
		return invertedMap;
	}

	public HashMap<Column, Integer> getInvertedGroup() {
		return invertedGroup;
	}

	public ArrayList<Table> getDimensionTable() {
		return dimensionTables;
	}

}
