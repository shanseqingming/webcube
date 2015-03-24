package cn.edu.zju.webcube.shared;

import cn.edu.zju.webcube.shared.db.Table;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * the tables participating in the new cube
 * @author wusai
 *
 */
public class CubeTableDataSource extends DataSource{

	private static CubeTableDataSource instance = null;
	
	public static CubeTableDataSource getInstance(){
		if(instance == null)
			instance = new CubeTableDataSource();
		return instance;
	}
	
	public CubeTableDataSource(){
		setID("cubetable");
		DataSourceField keyField = new DataSourceTextField("tablemember", "Table Member", 100);
		keyField.setPrimaryKey(true);
		
		DataSourceField valueField = new DataSourceTextField("tablevalue", "Table Member", 100);
		setFields(keyField, valueField);
	    setClientOnly(true);
	}
	
	public void addRecord(Table table){
		ListGridRecord record = new  ListGridRecord();
		record.setAttribute("tablemember", table.getName());
		record.setAttribute("tablevalue", table.getName());
		addData(record);
	}
	
	public void clean(){
		setCacheData(new Record[0]);
	}
}
