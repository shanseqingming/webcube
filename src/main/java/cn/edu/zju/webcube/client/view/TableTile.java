package cn.edu.zju.webcube.client.view;

import java.util.ArrayList;

import cn.edu.zju.webcube.shared.CubeTableDataSource;
import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.Table;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.ChangedEvent;
import com.smartgwt.client.widgets.grid.events.ChangedHandler;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.smartgwt.client.widgets.viewer.DetailViewerField;


public class TableTile extends TileGrid{
	
	private DataSource columnInfo;
	//private final SelectItem selection = new SelectItem();
	
	private final CubeView parent;
	public TableTile(CubeView myparent){
		//generateDataSource("c_1|c_2|c_3");
		this.parent = myparent;
	}

	@Override
    public Canvas getTile(int idx) {
        Canvas canvas = super.getTile(idx);

        /*
        DynamicForm form = new DynamicForm();
        form.setWidth100();
        form.setHeight(200);
        form.setTop(135);
        */
		int len = super.getData().length;
        TileRecord record = getTileRecord(canvas);
        final String id = record.getAttribute("Name");
		//MetaDataBuffer.getInstance().delTableToNewCube(id);
        Table table = MetaDataBuffer.getInstance().addTableToNewCube(id);

		if(table != null) {
			super.removeTile(idx);
		}
		else {
				return canvas;
		}

        if(table!=null){
        	ArrayList<Column> columns = table.getAllColumns();
        	generateDataSource(columns);
        	//parent.addNewJoinTable(table);
        	CubeTableDataSource.getInstance().addRecord(table);
        }
        final ListGrid columnInfoGrid = new ListGrid();  
        columnInfoGrid.setWidth100();
        columnInfoGrid.setHeight(220); 
        columnInfoGrid.setTop(150);
        columnInfoGrid.setDataSource(columnInfo);  
        columnInfoGrid.setCanEdit(true);  
        columnInfoGrid.setAlwaysShowEditors(true);  
        columnInfoGrid.setShowAllRecords(true);
        
        /*
        ListGridField columnName = new ListGridField("ColumnName", "Column Name");  
        ListGridField isOrdinaryColumn = new ListGridField("OrdinaryColumn", "Ordinary Column?");  
        ListGridField isQuantityColumn = new ListGridField("QuantityColumn", "Quantity Column?");  
        columnInfoGrid.setFields(columnName, isOrdinaryColumn, isQuantityColumn);
        
        columnInfoGrid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {

			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				// TODO Auto-generated method stub
				ListGridRecord[] records = columnInfoGrid.getRecords();
				ArrayList<String> ordinary = new ArrayList<String>();
				ArrayList<String> quantity = new ArrayList<String>();
				for(int i=0; i<records.length; i++){
					String cname = records[i].getAttribute("ColumnName");
					boolean isOrdinary = records[i].getAttributeAsBoolean("OrdinaryColumn");
					boolean isQuantity = records[i].getAttributeAsBoolean("QuantityColumn");
					if(isOrdinary){
						ordinary.add(cname);
					}
					if(isQuantity){
						quantity.add(cname);
					}
				}
				MetaDataBuffer.getInstance().setUpNewCube(id, ordinary, quantity);
			}
        });
        */
        ListGridField columnName = new ListGridField("ColumnName", "Column Name"); 
        columnName.setCanEdit(false);
        ListGridField columnType = new ListGridField("ColumnType", "Column Type");  
        columnType.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				// TODO Auto-generated method stub
				String newValue = (String)event.getValue();
				int rowid = event.getRowNum();
				Record record = columnInfoGrid.getRecord(rowid);
				String cname = record.getAttribute("ColumnName");
				if(newValue.equals("Ordinary Column")){
					MetaDataBuffer.getInstance().deleteQuantityForNewCube(id, cname);
					MetaDataBuffer.getInstance().insertOrdinaryForNewCube(id, cname);
				}
				else if(newValue.equals("Quantity Column")){
					MetaDataBuffer.getInstance().deleteOrdinaryForNewCube(id, cname);
					MetaDataBuffer.getInstance().insertQuantityForNewCube(id, cname);
				}
			}
        	
        });
        
        columnInfoGrid.setFields(columnName, columnType);
        columnInfoGrid.setAutoFetchData(true);  
        columnInfoGrid.setCanEdit(true);  
        columnInfoGrid.setEditEvent(ListGridEditEvent.CLICK);
		columnInfoGrid.setHeaderSpanTitle(table.getName(),"dd");
		columnInfoGrid.setTitle(table.getName());
		//DetailViewerField name = new DetailViewerField("Name");
		//name.setValue(table.getName());
		//canvas.addChild(form);
		//canvas.addChild(name);
		canvas.addChild(columnInfoGrid);
		
		parent.enableJoinSetting();
		
        return canvas;
    }
	
	public void generateDataSource(String serializedColumnData){
		
		columnInfo = new DataSource();
		
		DataSourceTextField itemNameField = new DataSourceTextField("ColumnName", "Column Name", 100, true);  
		itemNameField.setPrimaryKey(true);
		DataSourceField typeField = new DataSourceTextField("ColumnType", "Column Type");
		typeField.setValueMap("Not Included", "Ordinary Column", "Quantity Column");
		//DataSourceField quantityField = new DataSourceTextField("TypeValue", "Type Value");
		
		
		columnInfo.setFields(itemNameField, typeField);
		columnInfo.setClientOnly(true);
		
		String[] columns = serializedColumnData.split("\\|");
		for(int i=0; i<columns.length; i++){
			if(columns[i].trim().length()>0){
				ListGridRecord column = new  ListGridRecord();
				column.setAttribute("ColumnName", columns[i].trim());
				column.setAttribute("ColumnType", "Not Included");
				//column.setAttribute("QuantityColumn", false);
				columnInfo.addData(column);
			}
		}
	}
	
	public void generateDataSource(ArrayList<Column> columns){
		
		columnInfo = new DataSource();
		
		DataSourceTextField itemNameField = new DataSourceTextField("ColumnName", "Column Name", 100, true);  
		itemNameField.setPrimaryKey(true);
		DataSourceField typeField = new DataSourceTextField("ColumnType", "Column Type");
		typeField.setValueMap("Not Included", "Ordinary Column", "Quantity Column");
		
		
		columnInfo.setFields(itemNameField, typeField);
		columnInfo.setClientOnly(true);
		
		for(Column column : columns){
			ListGridRecord columnRecord = new  ListGridRecord();
			columnRecord.setAttribute("ColumnName", column.getName());
			columnRecord.setAttribute("ColumnType", "Not Included");
			columnInfo.addData(columnRecord);
		}
	}
	
	public void reset(){
		RecordList list = getRecordList();
		for (int i = list.getLength()-1; i >=0 ; i--) 
			removeData(list.get(i));
	}
	
	/**
	 * update the table information to the buffer
	 */
	public void updateTableInfo(){

	}
	
	class ColumnTypeDataSource extends DataSource{
		
		public ColumnTypeDataSource(){
			DataSourceField keyField = new DataSourceTextField("ColumnType", "Column Type");
			keyField.setPrimaryKey(true);
			
		    DataSourceField valueField = new DataSourceTextField("TypeValue", "Type Value");

		    setFields(keyField, valueField);
		    setClientOnly(true);
		    
		    ListGridRecord record = new  ListGridRecord();
			record.setAttribute("ColumnType", "QuantityColumn");
			record.setAttribute("TypeValue", "QuantityColumn");
			addData(record);
			
			record = new  ListGridRecord();
			record.setAttribute("ColumnType", "OrdinaryColumn");
			record.setAttribute("TypeValue", "OrdinaryColumn");
			addData(record);
		}
	}
	
}
