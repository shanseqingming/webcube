package cn.edu.zju.webcube.shared.msg;

import java.util.ArrayList;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.HiveDataType;
import cn.edu.zju.webcube.shared.db.Table;

/**
 * message for table information
 * 
 * @author wusai
 *
 */
public class TableInfoMessage implements Message {

	private Table table;

	public TableInfoMessage(String serializedData) {
		parse(serializedData);
	}

	public TableInfoMessage(Table t) {
		table = t;
	}

	public Table getTable() {
		return table;
	}

	
	@Override
	public void parse(String serializedData) {
		
		String[] values = serializedData.split("\\|");
		table = new Table(values[0].trim());

		int NumberOfColumns = Integer.parseInt(values[1]);

		for (int i = 2; i < 2 + NumberOfColumns; i++) {
			int idx = values[i].indexOf(":");
			String stype = values[i].substring(idx + 1, values[i].length());
			/**
			HiveDataType type = HiveDataType.STRING;
			switch (stype) {
				case "tinyint":
				case "smallint":
				case "int":
				case "bigint":			
				case "decimal":
					type = HiveDataType.INT;
					break;
				case "float":
					type = HiveDataType.FLOAT;
					break;
				case "date":
				case "timestamp":
					type = HiveDataType.TIMESTAP;
					break;
				case "string":
				case "varchar":
				case "char":
					type = HiveDataType.STRING;
					break;
				case "boolean":
					type = HiveDataType.BOOLEAN;
					break;
			}**/
			Column column = new Column(table.getName(), values[i].substring(0,
					idx), stype);
			table.addColumn(column);
			
		}
	}

	@Override
	public String serialize() {
		String reval = "";
		reval = table.getName() + "|" + table.getNumberOfColumns() + "|";
		ArrayList<Column> columns = table.getAllColumns();
		for (int i = 0; i < table.getNumberOfColumns(); i++) {
			Column column = columns.get(i);

			reval += column.getName() + ":";
			reval += column.getType()+"|";
		}

		return reval;
	}

}
