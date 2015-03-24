package cn.edu.zju.webcube.shared.db;

/**
 * a specific column of a table
 * 
 * @author wusai
 *
 */
public class Column {

	private String tableName;

	private String name; // column name

	private HiveDataType dataTyp = null;

	private CubeColumnType clnTyp = null; //group or quantity

	private CubeAggregationType aggTyp = null; // optional should be avg sum

	public Column(String tname, String name, String type) {
		
		this(tname, name, HiveDataType.valueOf(type.toUpperCase()));
	}

	public Column(String tname, String name, HiveDataType dataTyp) {
		tableName = tname;
		this.name = name;
		this.dataTyp = dataTyp;
	}
	
	
	public Column(String columnDetailStr) {
		//column=time.year.string.group.null 
		String[] columnAttr = columnDetailStr.split("\\.");
		if (columnAttr.length != 5)
			return;

		this.tableName = columnAttr[0];
		this.name = columnAttr[1];
		this.dataTyp = HiveDataType.valueOf(columnAttr[2].toUpperCase());
		this.clnTyp = CubeColumnType.valueOf(columnAttr[3]);
		
		if(clnTyp == CubeColumnType.quantity) {
			this.aggTyp = CubeAggregationType.valueOf(columnAttr[4]);
		}
	}
	

	public String getTable() {
		return tableName;
	}

	public void setRequiredAgg(String aggOps) {
		aggTyp = CubeAggregationType.valueOf(aggOps);
	}
	
	public void setRequiredAgg(CubeAggregationType aggOps) {
		this.aggTyp = aggOps;
	}
	

	public String getRequiredAgg() {
		if (aggTyp == null)
			return null;
		else
			return aggTyp.toString();
	}

	public void setAsOrdinaryType() {
		clnTyp = CubeColumnType.group;
	}

	public void setAsQuantityType() {
		clnTyp = CubeColumnType.quantity;
	}

	public String getName() {
		return name;
	}

	public HiveDataType getType() {
		return dataTyp;
	}

	public boolean isCubeColumnTypeNull() {
		return clnTyp == null;
	}

	public boolean isOrdinary() {
		return clnTyp == CubeColumnType.group;
	}

	public boolean isQuantity() {
		return !isOrdinary();
	}

	public static Column copyOf(Column c) {
		Column n = new Column(c.getTable(), c.getName(), c.getType());

		if (c.isCubeColumnTypeNull() == false) {

			if (c.isOrdinary()) {
				n.setAsOrdinaryType();
			} else {
				n.setAsQuantityType();
				if (c.getRequiredAgg() != null) {
					n.setRequiredAgg(c.getRequiredAgg());
				}
			}
		}
		return n;
	}
	
	
	public String toString(){

		String str = tableName + "." + name + "." + dataTyp + "." + clnTyp + "." + aggTyp;

		return str;
		
	}
	
	
	public boolean compare(Column c) {
		String cstr = c.toString();
		return toString().equalsIgnoreCase(cstr);
	}
	
	public CubeColumnType getCubeColumnType() {
		return clnTyp;
	}

			
}
