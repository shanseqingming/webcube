package cn.edu.zju.webcube.client.view;


import cn.edu.zju.webcube.shared.CubeTableDataSource;
import cn.edu.zju.webcube.shared.ForeignKeyDataSource;
import cn.edu.zju.webcube.shared.PrimaryKeyDataSource;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.msg.CubeInfoMessage;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class JoinConditionView extends HLayout{

	final ListGrid joinGrid = new ListGrid();  
	
	final IButton joinButton = new IButton("Add New Join Condition");
	
	final IButton submitButton = new IButton("Create the Cube");
	
	final IButton reset = new IButton("Reset");
	
	//SelectItem pkeyColumns =  new SelectItem();
	
	//final SelectItem fkeyColumns =  new SelectItem();
	
	final SelectItem factTable = new SelectItem();
	
	final DynamicForm form = new DynamicForm();
	
	//private LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
	
	private final CubeView parent;
	
	private ForeignKeyDataSource fksource;
	
	
	public JoinConditionView(CubeView myparent){
		
		this.parent = myparent;
		
		PrimaryKeyDataSource pksource = new PrimaryKeyDataSource();
		SelectItem pkeyColumns =  new SelectItem(); 
		pkeyColumns.setDefaultToFirstOption(true);
		pkeyColumns.setType("comboBox");
		pkeyColumns.setAllowEmptyValue(false);
		//setUpSelectValues();
		//allColumns.setValueMap(valueMap);
		pkeyColumns.setOptionDataSource(pksource);
		
		fksource = new ForeignKeyDataSource();
		SelectItem fkeyColumns =  new SelectItem();
		fkeyColumns.setDefaultToFirstOption(true);
		fkeyColumns.setType("comboBox");
		fkeyColumns.setAllowEmptyValue(false);
		//setUpSelectValues();
		//allColumns.setValueMap(valueMap);
		fkeyColumns.setOptionDataSource(fksource);
		
		//DynamicForm form = new DynamicForm();
		form.setNumCols(2);  
        form.setColWidths(100, "*");
        
		factTable.setDefaultToFirstOption(false);
		factTable.setType("comboBox");
		factTable.setWidth(200);
		factTable.setName("isfact");
		factTable.setTitle("select a fact table");
		factTable.setValueField("tablemember");
		factTable.setDisplayField("tablevalue");
		factTable.setAllowEmptyValue(true);
		factTable.setDisabled(true);
		factTable.setOptionDataSource(CubeTableDataSource.getInstance());
		joinGrid.setWidth(340);  
		joinGrid.setAutoFitMaxRecords(4);  
		joinGrid.setAutoFitData(Autofit.VERTICAL);  
		joinGrid.setCanEdit(false);  
		joinGrid.setEditEvent(ListGridEditEvent.CLICK);  
		joinGrid.setListEndEditAction(RowEndEditAction.NEXT);
		
		//joinGrid.setAutoFetchData(true);
		
		ListGridField firstColumn = new ListGridField("pvalue", "Primary Key");  
        firstColumn.setEditorProperties(pkeyColumns);
        firstColumn.setDisplayField("pvalue");
		ListGridField secondColumn = new ListGridField("fvalue", "Foreign Key"); 
		secondColumn.setEditorProperties(fkeyColumns);
		secondColumn.setDisplayField("fvalue");
        
        joinGrid.setFields(firstColumn, secondColumn);
        
    	factTable.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				//SC.warn("got it");
				ListGridRecord selection = factTable.getSelectedRecord();
				String table = selection.getAttribute("tablevalue");
				MetaDataBuffer.getInstance().setFactTable(table);
				
				
				RecordList list = joinGrid.getRecordList();
				for (int i = list.getLength()-1; i >=0 ; i--) 
					joinGrid.removeData(list.get(i));
								
				joinButton.setDisabled(false);
				joinGrid.setCanEdit(true);	
				reloadPrimaryKeys();
				//joinGrid.fetchData();
				//pkeyColumns.fetchData();
			}
			
		});
        
        joinButton.setDisabled(true);
        joinButton.addClickHandler(new ClickHandler() {  
            @Override
			public void onClick(ClickEvent event) {   
            	fksource.update();
            	joinGrid.startEditingNew(); 
            }  
        });  
        
        reset.addClickHandler(new ClickHandler() {  
            @Override
			public void onClick(ClickEvent event) {  
            	parent.reset(); 
            }  
        });  
        
        submitButton.setDisabled(true);
        submitButton.addClickHandler(new ClickHandler(){
        	@Override
			public void onClick(ClickEvent event){
        		submitCubeQuery();
        	}
        });
        
        VLayout leftvlayout = new VLayout();
        leftvlayout.setWidth(380);
        leftvlayout.setHeight(120);
        
        form.setWidth(380);
        form.setFields(factTable);
        leftvlayout.addMember(joinGrid);
        leftvlayout.addMember(form);
        
        VLayout rightvlayout = new VLayout();
        rightvlayout.setWidth(100);
        rightvlayout.addMember(joinButton);
        rightvlayout.addMember(submitButton);
        rightvlayout.addMember(reset);
        
        addMember(leftvlayout);
        addMember(rightvlayout);
        this.setWidth(480);
        this.setHeight(120);
        this.setLayoutAlign(Alignment.CENTER);
        this.setBorder("1px solid blue");
	}
	
	public void reloadPrimaryKeys(){
		//pkeyColumns.clearValue();
		
		PrimaryKeyDataSource pksource = new PrimaryKeyDataSource();
		SelectItem pkeyColumns =  new SelectItem();
		pkeyColumns.setDefaultToFirstOption(true);
		pkeyColumns.setType("comboBox");
		pkeyColumns.setAllowEmptyValue(false);
		pkeyColumns.setOptionDataSource(pksource);
		pksource.updateRecord();
		
		SelectItem fkeyColumns =  new SelectItem();
		fkeyColumns.setDefaultToFirstOption(true);
		fkeyColumns.setType("comboBox");
		fkeyColumns.setAllowEmptyValue(false);
		fkeyColumns.setOptionDataSource(fksource);
		
		
		ListGridField newColumn1 = new ListGridField("pvalue", "Primary Key");  
		newColumn1.setEditorProperties(pkeyColumns);
		newColumn1.setDisplayField("pvalue");
		ListGridField newColumn2 = new ListGridField("fvalue", "Foreign Key"); 
		newColumn2.setEditorProperties(fkeyColumns);
		newColumn2.setDisplayField("fvalue");

		//joinGrid.invalidateRecordComponents();
		//joinGrid.fetchData();
        joinGrid.setFields(newColumn1, newColumn2);
        joinGrid.redraw();
	}
	
	public void reset(){
		//valueMap.clear();
		RecordList list = joinGrid.getRecordList();
		for (int i = list.getLength()-1; i >=0 ; i--) 
			joinGrid.removeData(list.get(i));
		factTable.setDisabled(true);
		factTable.clearValue();
		fksource = new ForeignKeyDataSource();
		//PrimaryKeyDataSource.getInstance().clean();
		CubeTableDataSource.getInstance().clean();
		MetaDataBuffer.getInstance().clearNewCube();
		joinButton.setDisabled(true);
		submitButton.setDisabled(true);
		reset.setDisabled(true);
	}
	
	public void enableEdit(){
		submitButton.setDisabled(false);
		reset.setDisabled(false);
		factTable.setDisabled(false);
		//form.getField("isfact").invalidateDisplayValueCache();
		//factTable.setOptionDataSource(CubeTableDataSource.getInstance());	
		factTable.fetchData();
		//factTable.setDefaultToFirstOption(true);
		
		//ListGridRecord selection = factTable.getSelectedRecord();
		//String table = selection.getAttribute("tablevalue");
		//MetaDataBuffer.getInstance().setFactTable(table);
		//PrimaryKeyDataSource.getInstance().updateRecord();
	}
	
	/**
	 * submit the cube request to hive server
	 */
	public void submitCubeQuery(){
		ListGridRecord frecord = factTable.getSelectedRecord();
		String factName = frecord.getAttribute("tablevalue");
		if(factName==null){
			SC.warn("Please Specify a Fact Table!");
			return;
		}
		MetaDataBuffer.getInstance().setFactTable(factName);
		
		final Dialog dialogProperties = new Dialog();  
        dialogProperties.setWidth(300);  
        SC.askforValue("Question", "Please Input a Cube Name?", "", new ValueCallback() {  
            @Override  
            public void execute(String value) {  
                if (value != null) {  
                	
                	if(MetaDataBuffer.getInstance().isCubeExist(value)){
                		SC.warn("Cube with the Same Name Exists. Please Use Another Name.");
                	}
                	else{
                		ListGridRecord[] records = joinGrid.getRecords();
                		for(int i=0; i<records.length; i++){
                			String primarykey = records[i].getAttribute("pvalue");
                			int idx = primarykey.indexOf('.');
                			primarykey = primarykey.substring(idx+1, primarykey.length());
                			String foreignkey = records[i].getAttribute("fvalue");
                			idx = foreignkey.indexOf('.');
                			String dimtable = foreignkey.substring(0, idx);
                			
                			if(!dimtable.equals(MetaDataBuffer.getInstance().getFactTable().getName())){
                				//ignore the self-join
                				foreignkey = foreignkey.substring(idx+1, foreignkey.length());
                				
                				MetaDataBuffer.getInstance().addNewJoinCondition(dimtable, primarykey, foreignkey);
                			}
                		} 
                		String sql = MetaDataBuffer.getInstance().generateNewCube(value);
                		//for test
                		if(sql!=null){
                			//SC.warn(sql); 
                			
                			/**
                			 * TODO submit query to hive
                			 */
                			CubeInfoMessage cubeMessage = new CubeInfoMessage(MetaDataBuffer.getInstance().getNewCube());
                			parent.queryService.createCube(sql, cubeMessage.serialize(), new AsyncCallback<Boolean>(){

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									SC.warn("Fail to connect Server. Please check the network connection.");
								}

								@Override
								public void onSuccess(Boolean result) {
									// TODO Auto-generated method stub
									if(result)
										SC.warn("The Cube Job is submitted to the sever. Please check the progress later.");
									else
										SC.warn("The Server fails to process the request!");
								}
                				
                			});
                			
                			parent.reset(); 
                		}
                		else
                			SC.warn("Please Select at least One Column for the Cube!");
                	}
                	
                }  
            }  
        }, dialogProperties);  
	}
	
	
	/**
	 * for testing only
	 */
	/**
	private void setUpSelectValues(){
		int tableNumber = 3;
		int columnNumber = 3;
		for(int i=0; i<tableNumber; i++){
			String tname = "table_" + i;
			for(int j=0; j<columnNumber; j++){
				String cname = "c_" + j;
				valueMap.put(tname+"."+cname, tname+"."+cname);
			}
		}
	}
	**/
}
