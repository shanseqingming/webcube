package cn.edu.zju.webcube.client.view;

import java.util.ArrayList;

import cn.edu.zju.webcube.client.service.HiveQueryServiceAsync;
import cn.edu.zju.webcube.shared.CaculusDataSource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;

/**
 * UI for querying cubes
 * @author wusai
 *
 */
public class QueryView {
	
	private HiveQueryServiceAsync queryService = null;
	
	private String cubeID;
	
	public QueryView(HiveQueryServiceAsync service, String cubeID){
		queryService = service;
		this.cubeID = cubeID;
	}

	public Canvas createSplitPane() {    
		final SplitPane splitPane = new SplitPane();    
        splitPane.setAutoDraw(Boolean.FALSE);    
        splitPane.setNavigationTitle("Available Caculus");  
        splitPane.setDetailTitle("Query Form (Create a New Query By Dragging Caculus Here)");
        splitPane.setShowLeftButton(false);    
        splitPane.setShowRightButton(false);    
        splitPane.setBorder("1px solid blue");
        String cubename = cubeID.substring(cubeID.indexOf(':')+1);

        final TreeGrid navigationPane = new TreeGrid();    
        navigationPane.setAutoDraw(Boolean.TRUE);    
        navigationPane.setDataSource(new CaculusDataSource(cubename));
        navigationPane.setAutoFetchData(Boolean.TRUE);    
        navigationPane.setLoadDataOnDemand(true); 
        navigationPane.setCanEdit(false); 
        navigationPane.setCanReorderRecords(false); 
        navigationPane.setCanAcceptDroppedRecords(false); 
        navigationPane.setCanDragRecordsOut(true);
        navigationPane.setDragDataAction(DragDataAction.COPY);
        navigationPane.setTreeFieldTitle("CaculusName");
        navigationPane.setExpansionMode(ExpansionMode.DETAILS);
        //navigationPane.setShowHeader(Browser.getIsDesktop()); 
        TreeGridField nameField = new TreeGridField("CaculusName", "Caculus Name", 350); 
        nameField.setTreeField(true);
        navigationPane.setFields(nameField);

        ResultListView detailLayout = new ResultListView(this);
                
        splitPane.setDetailPane(detailLayout);
        splitPane.setNavigationPane(navigationPane);    
         
        splitPane.setWidth100();
        splitPane.setHeight100();
        
        return splitPane;    
	
	
	}
	
	public void submitQuery(String cubeID, ArrayList<String> ordinaryColumns, ArrayList<String> quantityColumns){
		
		queryService.submitQuery(cubeID, ordinaryColumns, quantityColumns, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				SC.warn("Fail to connect to hive server. Please check the network connection.");
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public String getCubeID(){
		return cubeID;
	}
}
