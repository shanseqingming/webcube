package cn.edu.zju.webcube.shared;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.HiveDataType;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.Table;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * 
 * @author wusai
 *
 */
public class TableListDataSource extends DataSource{
	
	private static TableListDataSource instance = null;
	
	public static TableListDataSource getInstance() {  
		if (instance == null) {  
	        instance = new TableListDataSource("tableListDS");  
        }  
		return instance;  
	}

	 public TableListDataSource(String id){
		 setID(id);  
		 
		 DataSourceTextField itemKey = new DataSourceTextField("key", null);
		 itemKey.setHidden(true);
		 itemKey.setRequired(true);
		 itemKey.setPrimaryKey(true);
		 
		 DataSourceTextField itemNameField = new DataSourceTextField("Name", "Table Name", 150, true);  
	 
	     DataSourceTextField itemTypeField = new DataSourceTextField("Type", "Type", 100, true);  
	     
	     DataSourceImageField pictureField = new DataSourceImageField("tablepicture", "Picture");  
	     
	     DataSourceTextField tableField = new DataSourceTextField("tableName", null);  
	     tableField.setHidden(true);
	     tableField.setRequired(true);
	     tableField.setRootValue(0);
	     tableField.setForeignKey("database.key");
	     setFields(itemKey, itemNameField, itemTypeField, pictureField, tableField);  
	     
	     
	     
	     setClientOnly(true);
	 }
	 
	 public void parseData(){            //get the information that will fill the left navigation in the client interface
		 
		 HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
		 
		 int keyCount = 0;               //the root folder's id
		 
		 ListGridRecord root = new  ListGridRecord();
		 root.setAttribute("key", keyCount);
		 root.setAttribute("Name", "database");
		 root.setCanDrag(false);
		 this.addData(root);
		 
		 
		 
		 keyCount++;
		 MetaDataBuffer dataBuffer = MetaDataBuffer.getInstance();
		 ArrayList<Table> tables = dataBuffer.getAllTables();
		 
		 for(int i=0; i<tables.size(); i++){                     //Get the table information
			 ArrayList<Column> columns = tables.get(i).getAllColumns();
			 
			 ListGridRecord tableR = new ListGridRecord();
			 tableR.setAttribute("Name",tables.get(i).getName());
			 tableR.setAttribute("tableName", 0);
			 tableR.setAttribute("key", keyCount);
			 tableR.setAttribute("tablepicture", "table.jpg");
			 tableR.setCanDrag(true);
			 this.addData(tableR);
			 keyMap.put(tables.get(i).getName(), keyCount);
			 keyCount++;
			 
			 for(int j=0; j<columns.size(); j++){            //Get the column information of a table
				 Column column = columns.get(j);
				 
				 ListGridRecord columnR = new ListGridRecord();
				 columnR.setAttribute("Name", column.getName());
				 columnR.setAttribute("Type", column.getType().toString());
				 columnR.setAttribute("tableName", keyMap.get(column.getTable()));
				 columnR.setAttribute("key", keyCount);
				 columnR.setCanDrag(false);
				 keyCount++;
				 this.addData(columnR);
			 }
		 }		 
		 
	 }
	 
	 public void clean(){
		 setCacheData(new Record[0]);
		}
}
