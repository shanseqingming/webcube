package cn.edu.zju.webcube.shared.db;

public enum HiveDataType {
	BOOLEAN, INT, LONG, FLOAT, DOUBLE, STRING, TIMESTAP, BIGINT, TINYINT, ARRAY, MAP, STRUCT, BINARY;
	
	public String toString() {
		return this.name().toLowerCase();
	}
	
}

