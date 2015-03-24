package cn.edu.zju.webcube.shared;

import java.util.ArrayList;
import java.util.Random;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.Table;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;



public class ForeignKeyDataSource extends DataSource{
	
	/**
	private static ForeignKeyDataSource instance = null;
	
	public static ForeignKeyDataSource getInstance(){
		if(instance == null){
			instance = new ForeignKeyDataSource();
		}
		return instance;
	}
	**/

	private ArrayList<String> existTable = new ArrayList<String>();
	
	public ForeignKeyDataSource(){
		//this.setID("foreign_key");
		DataSourceField keyField = new DataSourceTextField("FJoinKey", "Foreign Key");
		keyField.setPrimaryKey(true);
		
	    DataSourceField valueField = new DataSourceTextField("fvalue", "value");

	    setFields(keyField, valueField);
	    setClientOnly(true);
	}
	
	
	public void addRecord(Table table){
		/**
		 * very bad implementation to avoid duplicate key warning
		 * by combining key with a random number
		 */
		
		Random rand = new Random(System.currentTimeMillis());
		
		for(Column column : table.getAllColumns()){
			if(!existTable.contains(table.getName()+"."+column.getName())){
				ListGridRecord record = new  ListGridRecord();
				record.setAttribute("FJoinKey", table.getName()+"."+column.getName() + "@" + rand.nextInt(1000000));
				record.setAttribute("fvalue", table.getName()+"."+column.getName());
				this.addData(record);
				existTable.add(table.getName()+"."+column.getName());
			}
		}
	}
	
	public void update(){
		ArrayList<Table> tables = MetaDataBuffer.getInstance().getTableInNewCube();
		for(Table table: tables){
			addRecord(table);
		}
	}
	
	public void clean(){
		setCacheData(new Record[0]);
	}
	

}
