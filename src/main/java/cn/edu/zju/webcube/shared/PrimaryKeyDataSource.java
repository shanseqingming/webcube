package cn.edu.zju.webcube.shared;

import java.util.Random;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.Table;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class PrimaryKeyDataSource extends DataSource{

	/*
	private static PrimaryKeyDataSource instance = null;
	
	public static PrimaryKeyDataSource getInstance(){
		if(instance == null){
			instance = new PrimaryKeyDataSource();
		}
		return instance;
	}
	*/
	
	public PrimaryKeyDataSource(){
		//this.setID("primary_key");
		DataSourceField keyField = new DataSourceTextField("PJoinKey", "Primary Key");
		keyField.setPrimaryKey(true);
		
	    DataSourceField valueField = new DataSourceTextField("pvalue", "value");

	    setFields(keyField, valueField);
	    setClientOnly(true);
	}
	

	
	/**
	 * very bad implementation to avoid duplicate key warning
	 * by combining key with a random number
	 */
	/*
	public void addRecord(Table table){
	
		
		Random rand = new Random(System.currentTimeMillis());
		
		
		for(Column column : table.getAllColumns()){
			ListGridRecord record = new  ListGridRecord();
			record.setAttribute("PJoinKey", table.getName()+"."+column.getName() +"@"+rand.nextInt(1000000));
			record.setAttribute("pvalue", table.getName()+"."+column.getName());
			this.addData(record);
		}
	}
	*/
	
	/**
	 * only show the fact table columns
	 */
	public void updateRecord(){
		clean();
		Table factTable = MetaDataBuffer.getInstance().getFactTable();
		Random rand = new Random(System.currentTimeMillis());
		if(factTable!=null){
			for(Column column : factTable.getAllColumns()){
				ListGridRecord record = new  ListGridRecord();
				record.setAttribute("PJoinKey", factTable.getName()+"."+column.getName() +"@"+rand.nextInt(1000000));
				record.setAttribute("pvalue", factTable.getName()+"."+column.getName());
				this.addData(record);
			}
		}
	}
	
	public void clean(){
		this.invalidateCache();
		setCacheData(new Record[0]);
	}
}
